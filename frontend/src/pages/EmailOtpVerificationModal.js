import React, { useState, useEffect, useRef, useCallback } from 'react';
import { X} from 'lucide-react';

/**
 * A modal component for email and OTP verification.
 * It manages its own internal state for the verification flow (email input, OTP input, timer).
 *
 * @param {object} props - The component's props.
 * @param {boolean} props.show - Controls the visibility of the modal.
 * @param {function} props.onClose - Callback function invoked when the modal is requested to be closed.
 * @param {function(string)} props.onVerified - Callback function invoked with the verified email when OTP is successfully verified.
 * @param {string} props.token - Authentication token required for API calls to send/verify OTP (if backend requires it).
 * @param {function(string, string, number)} props.showTimedMessage - A utility function from the parent to display temporary messages (e.g., success/error banners).
 * @param {string} [props.initialEmail=''] - Optional email to pre-fill the email input field.
 * @param {string} props.triggerContext - Context of the OTP request (e.g., 'payment', 'delivery').
 */
const EmailOtpVerificationModal = ({
    show,
    onClose,
    onVerified,
    token,
    showTimedMessage,
    initialEmail = '',
    triggerContext
}) => {
    const [currentStep, setCurrentStep] = useState('emailInput');
    const [tempEmail, setTempEmail] = useState(initialEmail);
    const [otp, setOtp] = useState('');
    const [otpError, setOtpError] = useState('');
    const [isSendingOtp, setIsSendingOtp] = useState(false);
    const [isVerifyingOtp, setIsVerifyingOtp] = useState(false);
    const [otpTimer, setOtpTimer] = useState(0);
    const otpTimerRef = useRef(null);

    // Determine if the modal is dismissible
    const isDismissible = triggerContext !== 'payment';

    // Effect to update tempEmail if initialEmail prop changes
    useEffect(() => {
        if (initialEmail) {
            setTempEmail(initialEmail);
            localStorage.setItem('dealerMail', initialEmail);

        }
    }, [initialEmail]);

    // Effect for cleaning up the OTP timer when the component unmounts or modal closes
    useEffect(() => {
        if (!show && otpTimerRef.current) {
            clearInterval(otpTimerRef.current);
            setOtpTimer(0);
            setOtpError('');
            setOtp('');
            setTempEmail(initialEmail);
            setCurrentStep('emailInput');
        }
        return () => {
            if (otpTimerRef.current) {
                clearInterval(otpTimerRef.current);
            }
        };
    }, [show, initialEmail]);

    /**
     * Handles sending an OTP to the provided email address via the API Gateway.
     */
    const handleSendOtp = useCallback(async () => {
        if (!tempEmail || !/\S+@\S+\.\S+/.test(tempEmail)) {
            setOtpError('Please enter a valid email address.');
            return;
        }

        setIsSendingOtp(true);
        setOtpError('');
        
        if (otpTimerRef.current) {
            clearInterval(otpTimerRef.current);
            setOtpTimer(0);
        }

        try {
            const response = await fetch(`http://localhost:8888/api/otp/send?email=${encodeURIComponent(tempEmail)}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    ...(token && { 'Authorization': `Bearer ${token}` })
                }
            });

            if (response.ok) {
                showTimedMessage('OTP sent to your email!', 'success');
                setCurrentStep('otpInput');
                setOtpTimer(60);
                otpTimerRef.current = setInterval(() => {
                    setOtpTimer(prev => {
                        if (prev <= 1) {
                            clearInterval(otpTimerRef.current);
                            return 0;
                        }
                        return prev - 1;
                    });
                }, 1000);
            } else {
                const errorText = await response.text();
                setOtpError(errorText || 'Failed to send OTP. Please try again.');
                showTimedMessage(`Failed to send OTP: ${errorText || 'Unknown error'}`, 'error');
            }
        } catch (error) {
            console.error('Error sending OTP:', error);
            setOtpError('Network error. Could not send OTP.');
            showTimedMessage('Network error. Could not send OTP.', 'error');
        } finally {
            setIsSendingOtp(false);
        }
    }, [tempEmail, token, showTimedMessage]);

    /**
     * Handles verifying the entered OTP with the API Gateway.
     */
    const handleVerifyOtp = useCallback(async () => {
        if (!otp.trim()) {
            setOtpError('Please enter the OTP.');
            return;
        }

        setIsVerifyingOtp(true);
        setOtpError('');
        try {
            const response = await fetch(`http://localhost:8888/api/otp/verify?email=${encodeURIComponent(tempEmail)}&otp=${encodeURIComponent(otp)}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    ...(token && { 'Authorization': `Bearer ${token}` })
                }
            });

            if (response.ok) {
                const resultText = await response.text();
                if (resultText === "OTP Verified!") {
                    showTimedMessage('Email verified successfully!', 'success');
                    onVerified(tempEmail); // Call the parent's success handler
                    // Reset modal state for next time it opens
                    setCurrentStep('emailInput');
                    setOtpTimer(0);
                    clearInterval(otpTimerRef.current);
                    setOtpError('');
                    setOtp('');
                    setTempEmail(initialEmail);
                    onClose(); // Closes the modal
                } else {
                    setOtpError(resultText || 'Invalid or expired OTP!');
                    showTimedMessage(`Verification failed: ${resultText || 'Invalid or expired OTP!'}`, 'error');
                }
            } else {
                const errorText = await response.text();
                setOtpError(errorText || 'OTP verification failed. Please try again.');
                showTimedMessage(`Verification failed: ${errorText || 'Unknown error'}`, 'error');
            }
        } catch (error) {
            console.error('Error verifying OTP:', error);
            setOtpError('Network error. Could not verify OTP.');
            showTimedMessage('Network error. Could not verify OTP.', 'error');
        } finally {
            setIsVerifyingOtp(false);
        }
    }, [otp, tempEmail, token, showTimedMessage, onVerified, onClose, initialEmail]);

    // If 'show' prop is false, do not render the modal
    if (!show) {
        return null;
    }

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4"
             onClick={(e) => { if (isDismissible) onClose(); }} // Allow background click to close only if dismissible
        >
            <div className="bg-white p-8 rounded-lg shadow-xl w-full max-w-md relative animate-fade-in-up"
                 onClick={(e) => e.stopPropagation()} // Prevent clicks inside modal from closing it
            >
                {isDismissible && ( // Conditionally render the close button
                    <button
                        onClick={() => {
                            onClose();
                            setCurrentStep('emailInput');
                            setOtpTimer(0);
                            clearInterval(otpTimerRef.current);
                            setOtpError('');
                            setOtp('');
                            setTempEmail(initialEmail);
                        }}
                        className="absolute top-4 right-4 text-gray-500 hover:text-gray-700 transition-colors duration-200"
                    >
                        <X size={24} />
                    </button>
                )}
                <h2 className="text-2xl font-bold text-gray-800 mb-6 border-b pb-3">
                    {currentStep === 'emailInput' ? 'Enter Your Email' : 'Verify Your Email'}
                </h2>

                {currentStep === 'emailInput' && (
                    <div className="space-y-5">
                        <p className="text-gray-700">Please enter your email address to receive a one-time password (OTP) for verification.</p>
                        <div>
                            <label htmlFor="modalEmailInput" className="block text-sm font-medium text-gray-700 mb-1">Email Address</label>
                            <input
                                type="email"
                                id="modalEmailInput"
                                value={tempEmail}
                                onChange={(e) => setTempEmail(e.target.value)}
                                className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-green-500 focus:border-green-500 outline-none"
                                placeholder="your.email@example.com"
                                disabled={isSendingOtp}
                            />
                            {otpError && <p className="text-red-500 text-sm mt-2">{otpError}</p>}
                        </div>
                        <div className="mt-6 flex justify-end">
                            <button
                                onClick={handleSendOtp}
                                className="px-5 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
                                disabled={isSendingOtp || !tempEmail || !/\S+@\S+\.\S+/.test(tempEmail)}
                            >
                                {isSendingOtp ? 'Sending OTP...' : 'Send OTP'}
                            </button>
                        </div>
                    </div>
                )}

                {currentStep === 'otpInput' && (
                    <div className="space-y-5">
                        <p className="text-gray-700">An OTP has been sent to **{tempEmail}**. Please enter it below to verify your email.</p>
                        <div>
                            <label htmlFor="modalOtpInput" className="block text-sm font-medium text-gray-700 mb-1">One-Time Password (OTP)</label>
                            <input
                                type="text"
                                id="modalOtpInput"
                                value={otp}
                                onChange={(e) => setOtp(e.target.value)}
                                className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-green-500 focus:border-green-500 outline-none"
                                placeholder="Enter OTP"
                                disabled={isVerifyingOtp}
                                maxLength="6"
                            />
                            {otpError && <p className="text-red-500 text-sm mt-2">{otpError}</p>}
                        </div>
                        <div className="flex justify-between items-center mt-6">
                            {otpTimer > 0 ? (
                                <span className="text-gray-600 text-sm">Resend in {otpTimer}s</span>
                            ) : (
                                <button
                                    onClick={handleSendOtp}
                                    className="text-blue-500 hover:underline text-sm disabled:opacity-50 disabled:cursor-not-allowed"
                                    disabled={isSendingOtp}
                                >
                                    Resend OTP
                                </button>
                            )}
                            <button
                                onClick={handleVerifyOtp}
                                className="px-5 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
                                disabled={isVerifyingOtp || !otp.trim()}
                            >
                                {isVerifyingOtp ? 'Verifying...' : 'Verify OTP'}
                            </button>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default EmailOtpVerificationModal;
