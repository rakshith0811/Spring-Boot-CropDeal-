import React, { useState, useEffect, useCallback, useRef, useMemo } from 'react';
import { User, ShoppingCart, MessageCircle, X, Send, Package, Truck, MinusCircle, PlusCircle, CreditCard, Phone, MapPin, Trash2, CheckCircle, Clock } from 'lucide-react';
import { useLocation, useNavigate } from 'react-router-dom';
import EmailOtpVerificationModal from './EmailOtpVerificationModal';

const DealerDashboard = () => {
    // State management
    const [activeTab, setActiveTab] = useState('crops');
    const [showProfile, setShowProfile] = useState(false);
    const [showUpdateProfile, setShowUpdateProfile] = useState(false);
    const [showChatbot, setShowChatbot] = useState(false);
    const [loading, setLoading] = useState(false); // Used for general loading states (profile, cart, checkout)
    const [message, setMessage] = useState('');
    const [messageType, setMessageType] = useState(''); // 'success', 'error', 'warning'
    const [dealerId] = useState(localStorage.getItem('userId'));
    const [token] = useState(localStorage.getItem('token'));
    const [username] = useState(localStorage.getItem('username'));
    const [dealerMail]=useState(localStorage.getItem('dealerMail'));

    // --- OTP Modal State & related flags ---
    const [showOtpModal, setShowOtpModal] = useState(false);
    const [otpContext, setOtpContext] = useState(''); // 'payment' or 'delivery'
    const [otpEmail, setOtpEmail] = useState(''); // Email to send OTP to (from dealer profile)
    const [currentOrderToDeliver, setCurrentOrderToDeliver] = useState(null); // Stores the order being marked delivered
    const isPaymentOtpInitiatedRef = useRef(false); // Ref to prevent re-triggering payment OTP flow on re-renders

    // Profile data
    const [profile, setProfile] = useState(null);
    const [profileUpdate, setProfileUpdate] = useState({
        address: '',
        mobileNumber: ''
    });

    // Crops data
    const [crops, setCrops] = useState([]);
    const [cropQuantities, setCropQuantities] = useState({}); // Manages quantity for each crop before adding to cart

    // --- State for Search, Sort, Pagination ---
    const [searchTerm, setSearchTerm] = useState('');
    const [sortConfig, setSortConfig] = useState({ key: null, direction: 'ascending' });
    const [currentPage, setCurrentPage] = useState(1);
    const [itemsPerPage] = useState(6);

    // Cart data
    const [cartItems, setCartItems] = useState([]);

    // Orders data
    const [orders, setOrders] = useState([]);

    // Chatbot data
    const [chatMessages, setChatMessages] = useState([]);
    const [chatInput, setChatInput] = useState('');
    const [chatbotLoading, setChatbotLoading] = useState(false);

    // Ref to prevent duplicate Stripe redirect processing (less critical now, but safe to keep)
    const stripeRedirectProcessedRef = useRef(false);

    // Stripe Public Key
    const stripePublicKey = 'pk_test_51RSXrD4ClKhO9XuDgu9ENgGS575fmTRlM1XAuE64QTukoQpFDxCYVyJODA3UEvNeBZwhxaWoxoiL3vw5R0tDD04X00km1aSCkm';

    const location = useLocation();
    const navigate = useNavigate();

    // Load Stripe script dynamically
    useEffect(() => {
        const script = document.createElement('script');
        script.src = 'https://js.stripe.com/v3/';
        script.async = true;
        document.body.appendChild(script);

        return () => {
            document.body.removeChild(script);
        };
    }, []);

    // --- Utility Functions ---

    const showTimedMessage = useCallback((msg, type, duration = 3000) => {
        setMessage(msg);
        setMessageType(type);
        setTimeout(() => {
            setMessage('');
            setMessageType('');
        }, duration);
    }, []);

    const handleLogout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('userId');
        localStorage.removeItem('username');
        localStorage.removeItem('dealerMail');

        navigate('/', { replace: true });
    };

    const generateUniqueId = () => Math.random().toString(36).substring(2, 9) + Date.now().toString(36);

    // --- API Callbacks ---

    // Modified: fetchProfile now returns the profile data to be used by callers
    const fetchProfile = useCallback(async () => {
        if (!dealerId || !token) {
            showTimedMessage('Authentication details missing. Please log in again.', 'error');
            return null; // Return null if auth details are missing
        }
        setLoading(true);
        try {
            const response = await fetch(`http://localhost:8888/api/dealer/profile/${dealerId}`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (response.ok) {
                const data = await response.json();
                setProfile(data);
                setProfileUpdate({
                    address: data.user.address,
                    mobileNumber: data.user.mobileNumber
                });
                setOtpEmail(data.user.username); // Set email for OTP here from profile
                return data; // Return the fetched profile data
            } else {
                const errorData = await response.json();
                showTimedMessage(`Failed to fetch profile: ${errorData.message || 'Unknown error'}`, 'error');
                return null;
            }
        } catch (error) {
            console.error('Error fetching profile:', error);
            showTimedMessage('Error fetching profile. Please try again.', 'error');
            return null;
        } finally {
            setLoading(false);
        }
    }, [dealerId, token, showTimedMessage]);

    const handleUpdateProfile = async () => {
        setLoading(true);
        try {
            const response = await fetch(`http://localhost:8888/api/dealer/profile/${dealerId}`, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(profileUpdate)
            });
            if (response.ok) {
                showTimedMessage('Profile updated successfully!', 'success');
                setShowUpdateProfile(false);
                fetchProfile(); // Refresh profile data
            } else {
                const errorData = await response.json();
                showTimedMessage(`Failed to update profile: ${errorData.message || 'Unknown error'}`, 'error');
            }
        } catch (error) {
            console.error('Error updating profile:', error);
            showTimedMessage('Error updating profile. Please try again.', 'error');
        } finally {
            setLoading(false);
        }
    };

    const fetchCrops = useCallback(async () => {
        setLoading(true);
        try {
            const response = await fetch('http://localhost:8888/api/dealer/crops', {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (response.ok) {
                const data = await response.json();
                setCrops(data);
                const initialQuantities = {};
                data.forEach(crop => {
                    initialQuantities[crop.id] = 1; // Initialize quantities for all crops to 1
                });
                setCropQuantities(initialQuantities);
            } else {
                const errorData = await response.json();
                showTimedMessage(`Failed to fetch crops: ${errorData.message || 'Unknown error'}`, 'error');
            }
        } catch (error) {
            console.error('Error fetching crops:', error);
            showTimedMessage('Error fetching crops. Please try again.', 'error');
        } finally {
            setLoading(false);
        }
    }, [token, showTimedMessage]);

    const fetchCartItems = useCallback(async () => {
        if (!dealerId || !token) return [];
        setLoading(true);
        try {
            const response = await fetch(`http://localhost:8888/api/cart/${dealerId}`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (response.ok) {
                const data = await response.json();
                console.log('fetchCartItems: Successfully fetched cart data:', JSON.stringify(data, null, 2));
                setCartItems(data);
                return data; // Return the data for immediate use in other functions
            } else {
                const errorData = await response.json();
                showTimedMessage(`Failed to fetch cart items: ${errorData.message || 'Unknown error'}`, 'error');
                return [];
            }
        } catch (error) {
            console.error('Error fetching cart:', error);
            showTimedMessage('Error fetching cart items. Please try again.', 'error');
            return [];
        } finally {
            setLoading(false);
        }
    }, [dealerId, token, showTimedMessage]);

    const fetchOrders = useCallback(async () => {
        if (!dealerId || !token) return;
        setLoading(true);
        try {
            const response = await fetch(`http://localhost:8888/api/orders/getOrderByDealer/${dealerId}`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (response.ok) {
                const data = await response.json();
                setOrders(data);
            } else {
                const errorData = await response.json();
                showTimedMessage(`Failed to fetch orders: ${errorData.message || 'Unknown error'}`, 'error');
            }
        } catch (error) {
            console.error('Error fetching orders:', error);
            showTimedMessage('Error fetching orders. Please try again.', 'error');
        } finally {
            setLoading(false);
        }
    }, [dealerId, token, showTimedMessage]);

    const handleCropQuantityChange = useCallback((cropId, change) => {
        setCropQuantities(prev => {
            const currentQty = prev[cropId] || 1;
            const newQty = Math.max(1, currentQty + change);
            return { ...prev, [cropId]: newQty };
        });
    }, [setCropQuantities]);

    const handleAddToCart = async (cropId) => {
        const cropToAdd = crops.find(crop => crop.id === cropId);
        if (!cropToAdd) {
            console.error('Error: Selected crop not found in state for cropId:', cropId);
            showTimedMessage('Selected crop not found. Please refresh and try again.', 'error');
            return;
        }

        const quantity = cropQuantities[cropId] || 1;

        if (quantity > cropToAdd.cropQty) {
            showTimedMessage(`Cannot add ${quantity} kg. Only ${cropToAdd.cropQty} kg available for ${cropToAdd.cropName}.`, 'warning');
            return;
        }

        console.log('Attempting to add to cart. Data check:');
        console.log(' - cropId:', cropToAdd.id);
        console.log(' - quantity:', quantity);
        console.log(' - Full crop object (from state):', cropToAdd);
        console.log(' - Farmer ID (from crop):', cropToAdd.farmerId);

        if (cropToAdd.farmerId === undefined || cropToAdd.farmerId === null) {
            console.error('Error: Farmer ID missing for crop:', cropToAdd);
            showTimedMessage('Missing farmer information for this crop. Cannot add to cart. Please contact support.', 'error');
            setLoading(false);
            return;
        }

        setLoading(true);
        try {
            const response = await fetch(`http://localhost:8888/api/cart/add/${dealerId}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    cropId: cropToAdd.id,
                    quantity: quantity,
                    farmerId: cropToAdd.farmerId
                })
            });

            console.log('Frontend received API Response Status (Add to Cart):', response.status);
            console.log('Frontend received API Response OK property (Add to Cart):', response.ok);

            if (response.ok) {
                showTimedMessage('Crop added to cart successfully!', 'success');
                fetchCartItems(); // Refresh cart items to update the UI
            } else {
                const errorData = await response.json().catch(() => ({ message: 'No error message from server' }));
                console.error('Failed to add to cart. Backend response details:', errorData);
                showTimedMessage(`Failed to add to cart: ${errorData.message || 'Unknown server error. Please try again.'}`, 'error');
            }
        } catch (error) {
            console.error('Network or unexpected error while adding crop to cart:', error);
            showTimedMessage('An unexpected error occurred. Please check your internet connection or try again.', 'error');
        } finally {
            setLoading(false);
        }
    };

    const handleClearCart = useCallback(async () => {
        setLoading(true);
        try {
            const response = await fetch(`http://localhost:8888/api/cart/clear/${dealerId}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                setCartItems([]);
                showTimedMessage('Cart cleared successfully!', 'success');
                console.log('handleClearCart: Cart cleared successfully.');
                return true;
            } else {
                const errorData = await response.json();
                showTimedMessage(errorData.message || 'Failed to clear cart.', 'error');
                console.error('handleClearCart: Failed to clear cart:', errorData);
                return false;
            }
        } catch (error) {
            console.error('Clear cart error:', error);
            showTimedMessage('Error clearing cart.', 'error');
            return false;
        } finally {
            setLoading(false);
        }
    }, [dealerId, token, showTimedMessage]);

    // Function to mark an order as delivered via API call
    const markOrderAsDeliveredAPI = useCallback(async (orderId) => {
        setLoading(true);
        try {
            const response = await fetch(`http://localhost:8888/api/orders/updateOrderStatus/${orderId}`, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                showTimedMessage(`Order ${orderId?.slice(0, 8)}... marked as DELIVERED!`, 'success');
                fetchOrders(); // Refresh orders list
                return true;
            } else {
                const errorData = await response.json().catch(() => ({ message: 'No error message from server' }));
                showTimedMessage(`Failed to mark order as delivered: ${errorData.message || 'Unknown error'}`, 'error');
                console.error('markOrderAsDeliveredAPI: Failed to update order status:', errorData);
                return false;
                
            }
        } catch (error) {
            console.error('markOrderAsDeliveredAPI: Error updating order status:', error);
            showTimedMessage('Network error marking order as delivered.', 'error');
            return false;
        } finally {
            setLoading(false);
        }
    }, [token, showTimedMessage, fetchOrders]);


    const createOrdersAfterPaymentAndClearCart = useCallback(async (itemsToProcess, dealerEmail) => {
        console.log('createOrdersAfterPaymentAndClearCart: Initiated with itemsToProcess:', JSON.stringify(itemsToProcess, null, 2));
        if (!itemsToProcess || itemsToProcess.length === 0) {
            console.log("createOrdersAfterPaymentAndClearCart: No cart items to process for order creation.");
            return;
        }

        let allOrdersCreated = true;

        for (const item of itemsToProcess) {
            console.log('createOrdersAfterPaymentAndClearCart: Processing item:', JSON.stringify(item, null, 2));
            if (!item.farmerId || !item.cropId || !item.quantity) {
                console.error(`Skipping order creation for item due to missing farmerId, cropId, or quantity:`, item);
                allOrdersCreated = false;
                continue;
            }

            try {
                // --- 1. Create the Order Entry ---
                // Pass dealerEmail as a query parameter to the addOrder endpoint
                const orderResponse = await fetch(`http://localhost:8888/api/orders/addOrder?dealerEmail=${encodeURIComponent(dealerEmail)}`, {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        dealerId: dealerId,
                        farmerId: item.farmerId,
                        cropId: item.cropId,
                        quantity: item.quantity,
                        orderStatus: 'Out for delivery' // Initial status
                    })
                });

                if (!orderResponse.ok) {
                    const errorData = await orderResponse.json();
                    console.error(`Failed to add order for crop ${item.cropName || item.cropId}:`, errorData);
                    allOrdersCreated = false;
                    continue; // Skip quantity reduction if order creation failed
                } else {
                    const createdOrder = await orderResponse.json();
                    console.log(`Order created successfully for crop ${item.cropName || item.cropId}. Order ID: ${createdOrder.orderID}`);
                }

                // --- 2. Call endpoint to reduce crop quantity ---
                console.log(`Attempting to reduce quantity for crop ID: ${item.cropId}, quantity: ${item.quantity}`);
                const reduceQtyResponse = await fetch(`http://localhost:8888/api/dealer/crops/reduce-quantity/${item.cropId}?quantity=${item.quantity}`, {
                    method: 'PUT',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }
                });

                if (!reduceQtyResponse.ok) {
                    const errorData = await reduceQtyResponse.json();
                    console.error(`Failed to reduce quantity for crop ${item.cropId}:`, errorData);
                    showTimedMessage(`Error: Could not update stock for ${item.cropName || 'a crop'}. Please contact support.`, 'error');
                    allOrdersCreated = false; // Mark overall process as failed if quantity deduction fails
                } else {
                    console.log(`Quantity reduced successfully for crop ID: ${item.cropId}`);
                    fetchCrops(); // Re-fetch crops to update available quantities on the crops tab
                }

            } catch (error) {
                console.error(`Error adding order or reducing quantity for crop ${item.cropName || item.cropId}:`, error);
                allOrdersCreated = false;
            }
        }

        // --- Clear cart only if all orders were successfully created and quantities reduced ---
        if (allOrdersCreated) {
            console.log('createOrdersAfterPaymentAndClearCart: All orders created and quantities reduced. Clearing cart...');
            await handleClearCart();
            showTimedMessage('Payment successful! Orders have been placed and cart cleared.', 'success');
        } else {
            showTimedMessage('Payment successful but failed to create some orders or update stock. Please check your orders tab and contact support.', 'error');
        }
        // Always refresh orders after trying to create them, regardless of full success.
        fetchOrders();

    }, [dealerId, token, fetchCrops, fetchOrders, handleClearCart, showTimedMessage]);

    // Initiates Stripe redirect
    const handleCheckout = async () => {
        if (!cartItems || cartItems.length === 0) {
            showTimedMessage('Your cart is empty. Please add items before checking out.', 'warning');
            return;
        }
        if (!window.Stripe) {
            showTimedMessage('Stripe payment gateway not loaded. Please try again.', 'error');
            return;
        }

        setLoading(true);
        try {
            const totalAmount = cartItems.reduce((sum, item) => sum + item.totalPrice, 0);

            const response = await fetch('http://localhost:8888/api/payment/checkout', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    amount: Math.round(totalAmount),
                    quantity: 1, // This quantity is for the Stripe session, not individual crop quantity
                    name: 'Crop Purchase',
                    currency: 'inr'
                })
            });

            if (response.ok) {
                const data = await response.json();
                const sessionId = data.sessionId;

                const stripe = window.Stripe(stripePublicKey);
                const { error } = await stripe.redirectToCheckout({ sessionId });
                if (error) {
                    showTimedMessage(`Payment initiation failed: ${error.message}`, 'error');
                }
            } else {
                const errorData = await response.json();
                showTimedMessage(`Payment initiation failed: ${errorData.message || 'Unknown error'}`, 'error');
            }
        } catch (error) {
            console.error('Error during checkout:', error);
            showTimedMessage('Checkout error. Please try again.', 'error');
        } finally {
            setLoading(false);
        }
    };

    // Handler for clicking 'Mark as Delivered' button on an order
    const handleMarkAsDeliveredClick = (order) => {
        if (!profile || !profile.user || !profile.user.username) {
             showTimedMessage('Your profile is missing email information. Cannot proceed with OTP.', 'error');
             return;
         }
        setCurrentOrderToDeliver(order);
        setOtpContext('delivery');
        setOtpEmail(dealerMail); // Use the dealer's username (acting as email) for OTP
        setShowOtpModal(true); // Open the OTP verification modal
    };

    // Central handler for successful OTP verification
    const handleOtpVerified = useCallback(async (verifiedEmail) => { // CORRECTED: Accepts verifiedEmail
        setShowOtpModal(false); // Close OTP modal
        setLoading(true); // Set general loading state

        if (otpContext === 'payment') {
            console.log("handleOtpVerified: OTP verified for payment. Proceeding with order creation and cart clearing.");
            const currentCartItems = await fetchCartItems(); // Re-fetch cart to ensure up-to-date
            await createOrdersAfterPaymentAndClearCart(currentCartItems, verifiedEmail); // CORRECTED: Passes verifiedEmail
            // After successful payment processing, remove the query params
            navigate('/dealer-dashboard', { replace: true });
        } else if (otpContext === 'delivery' && currentOrderToDeliver) {
            console.log("handleOtpVerified: OTP verified for delivery. Marking order as delivered.");
            await markOrderAsDeliveredAPI(currentOrderToDeliver.orderID);
            setCurrentOrderToDeliver(null); // Clear the stored order
        }
        setLoading(false);
    }, [otpContext, currentOrderToDeliver, createOrdersAfterPaymentAndClearCart, fetchCartItems, markOrderAsDeliveredAPI, navigate]);


    // --- Search, Sort, Pagination Logic ---
    const processedCrops = useMemo(() => {
        let currentCrops = [...crops];

        if (searchTerm) {
            currentCrops = currentCrops.filter(crop =>
                crop.cropName.toLowerCase().includes(searchTerm.toLowerCase()) ||
                crop.cropType.toLowerCase().includes(searchTerm.toLowerCase()) ||
                (crop.cropDescription && crop.cropDescription.toLowerCase().includes(searchTerm.toLowerCase()))
            );
        }

        if (sortConfig.key) {
            currentCrops.sort((a, b) => {
                const aValue = a[sortConfig.key];
                const bValue = b[sortConfig.key];

                if (typeof aValue === 'string') {
                    return sortConfig.direction === 'ascending'
                        ? aValue.localeCompare(bValue)
                        : bValue.localeCompare(aValue);
                } else if (typeof aValue === 'number') {
                    return sortConfig.direction === 'ascending'
                        ? aValue - bValue
                        : bValue - aValue;
                }
                return 0;
            });
        }

        const indexOfLastItem = currentPage * itemsPerPage;
        const indexOfFirstItem = indexOfLastItem - itemsPerPage;
        const paginatedCrops = currentCrops.slice(indexOfFirstItem, indexOfLastItem);

        return paginatedCrops;
    }, [crops, searchTerm, sortConfig, currentPage, itemsPerPage]);


    const totalPages = useMemo(() => {
        let filteredCount = crops.length;
        if (searchTerm) {
            filteredCount = crops.filter(crop =>
                crop.cropName.toLowerCase().includes(searchTerm.toLowerCase()) ||
                crop.cropType.toLowerCase().includes(searchTerm.toLowerCase()) ||
                (crop.cropDescription && crop.cropDescription.toLowerCase().includes(searchTerm.toLowerCase()))
            ).length;
        }
        return Math.ceil(filteredCount / itemsPerPage);
    }, [crops, searchTerm, itemsPerPage]);

    const handleSearchChange = useCallback((e) => {
        setSearchTerm(e.target.value);
        setCurrentPage(1);
    }, []);

    const requestSort = useCallback((key) => {
        let direction = 'ascending';
        if (sortConfig.key === key && sortConfig.direction === 'ascending') {
            direction = 'descending';
        }
        setSortConfig({ key, direction });
        setCurrentPage(1);
    }, [sortConfig]);
    const handlePageChange = useCallback((pageNumber) => {
        setCurrentPage(pageNumber);
    }, []);

    const handleNextPage = useCallback(() => {
        if (currentPage < totalPages) {
            setCurrentPage(prev => prev + 1);
        }
    }, [currentPage, totalPages]);

    const handlePrevPage = useCallback(() => {
        if (currentPage > 1) {
            setCurrentPage(prev => prev - 1);
        }
    }, [currentPage]);
    // --- END NEW HANDLERS ---

    // --- General Effects ---

    // Effect to fetch data when the active tab changes
    useEffect(() => {
        if (activeTab === 'crops') fetchCrops();
        if (activeTab === 'cart') fetchCartItems();
        if (activeTab === 'orders') fetchOrders();
        // Fetch profile on explicit profile tab click or if not yet loaded
        // The payment success useEffect also triggers profile fetch if needed.
        if (activeTab === 'profile') {
             fetchProfile();
        }
    }, [activeTab, fetchCartItems, fetchCrops, fetchOrders, fetchProfile]);


    // Effect to handle payment status from URL (Stripe redirect) and trigger OTP modal
    useEffect(() => {
        const queryParams = new URLSearchParams(location.search);
        const paymentStatus = queryParams.get('payment');

        // Only proceed if paymentStatus is 'success' AND we haven't already initiated OTP for this payment
        if (paymentStatus === 'success' && !isPaymentOtpInitiatedRef.current) {
            isPaymentOtpInitiatedRef.current = true; // Set flag immediately to prevent re-entry

            const initiatePaymentOtpFlow = async () => {
                let currentProfile = profile;
                if (!currentProfile) {
                    console.log("Payment success detected, but profile not yet loaded. Fetching profile...");
                    currentProfile = await fetchProfile(); // Await the profile fetch
                }

                if (currentProfile && currentProfile.user && currentProfile.user.username) {
                    setOtpContext('payment');
                    setOtpEmail(currentProfile.user.username); // Use the username from the fetched profile
                    setShowOtpModal(true); // Open OTP modal for payment confirmation
                } else {
                    console.error("Failed to get dealer email from profile for OTP verification.");
                    showTimedMessage('Payment successful, but could not retrieve your email for verification. Please contact support.', 'error');
                    navigate('/dealer-dashboard', { replace: true }); // Clear URL even if OTP fails to trigger
                }
            };
            initiatePaymentOtpFlow(); // Call the async function
        } else if (paymentStatus === 'cancelled') {
            console.log("useEffect: Payment cancelled detected in URL.");
            showTimedMessage('Payment was cancelled by the user.', 'warning');
            navigate('/dealer-dashboard', { replace: true }); // Clean the URL
        } else if (paymentStatus && isPaymentOtpInitiatedRef.current) {
             // If paymentStatus is present but payment OTP was already handled, navigate to clear URL
            navigate('/dealer-dashboard', { replace: true });
        }
    }, [location.search, navigate, showTimedMessage, profile, fetchProfile]); // Added profile to dependencies

    // Reset payment processed flags when component unmounts
    useEffect(() => {
        return () => {
            console.log("Component unmounting or useEffect cleanup: Resetting paymentProcessedRef and isPaymentOtpInitiatedRef.");
            stripeRedirectProcessedRef.current = false;
            isPaymentOtpInitiatedRef.current = false;
        };
    }, []);

    // Effect for chatbot scroll to bottom
    useEffect(() => {
        const chatContainer = document.querySelector('.flex-1.p-3.overflow-y-auto');
        if (chatContainer) {
            chatContainer.scrollTop = chatContainer.scrollHeight;
        }
    }, [chatMessages]);

    // --- Chatbot Functions ---

    const handleChatSendMessage = async (e) => {
        e.preventDefault();
        if (!chatInput.trim()) return;

        const userMessage = { id: generateUniqueId(), type: 'user', text: chatInput };
        setChatMessages(prev => [...prev, userMessage]);
        setChatInput('');
        setChatbotLoading(true);

        try {
            const response = await fetch('http://localhost:8888/api/chat', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ message: userMessage.text })
            });

            if (response.ok) {
                const data = await response.json();
                const botMessage = { id: generateUniqueId(), type: 'bot', text: data.response };
                setChatMessages(prev => [...prev, botMessage]);
            } else {
                const errorData = await response.json();
                const errorMessage = { id: generateUniqueId(), type: 'bot', text: `Error: ${errorData.message || 'Could not get a response from the chatbot.'}` };
                setChatMessages(prev => [...prev, errorMessage]);
            }
        } catch (error) {
            console.error("Error sending message to chatbot:", error);
            const errorMessage = { id: generateUniqueId(), type: 'bot', text: 'Sorry, I encountered an error. Please try again.' };
            setChatMessages(prev => [...prev, errorMessage]);
        } finally {
            setChatbotLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gray-50 flex">
            {/* Sidebar */}
            <aside className="fixed top-0 left-0 h-screen w-64 bg-green-700 text-white shadow-md border-r border-green-800 p-4 flex flex-col z-40 overflow-y-auto">
                <div className="flex items-center mb-6 mt-2">
                    <h1 className="text-4xl font-bold text-white">CropDeal</h1>
                </div>
                <br/>
                <br/>
                <hr className="border-green-600" />
                <div className="my-4"></div>
                <nav className="flex-1 space-y-2">
                    <button
                        onClick={() => setActiveTab('crops')}
                        className={`w-full text-left py-2 px-3 rounded-md flex items-center text-sm font-medium transition-colors duration-200 ${
                            activeTab === 'crops'
                                ? 'bg-green-600 text-white'
                                : 'text-green-100 hover:bg-green-600 hover:text-white'
                        }`}
                    >
                        <Package className="mr-3" size={24} />
                        Available Crops
                    </button>
                    <div className="my-2"></div>
                    <br/><br/>
                    <button
                        onClick={() => setActiveTab('cart')}
                        className={`w-full text-left py-2 px-3 rounded-md flex items-center text-sm font-medium transition-colors duration-200 ${
                            activeTab === 'cart'
                                ? 'bg-green-600 text-white'
                                : 'text-green-100 hover:bg-green-600 hover:text-white'
                        }`}
                    >
                        <ShoppingCart className="mr-3" size={24} />
                        My Cart ({cartItems.length})
                    </button>
                    <div className="my-2"></div>
                    <br/><br/>
                    <button
                        onClick={() => setActiveTab('orders')}
                        className={`w-full text-left py-2 px-3 rounded-md flex items-center text-sm font-medium transition-colors duration-200 ${
                            activeTab === 'orders'
                                ? 'bg-green-600 text-white'
                                : 'text-green-100 hover:bg-green-600 hover:text-white'
                        }`}
                    >
                        <Truck className="mr-3" size={24} />
                        My Orders
                    </button>
                </nav>
            </aside>

            {/* Main Content Area */}
            <main className="flex-1 flex flex-col ml-64">
                {/* Header */}
                <header className="bg-white shadow-sm border-b border-gray-200">
                    <div className="max-w-full mx-auto px-4 sm:px-6 lg:px-8">
                        <div className="flex justify-between items-center h-16">
                            <div className="flex items-center">
                                <h1 className="text-2xl font-bold text-green-800">Dealer Dashboard</h1>
                            </div>
                            <div className="flex items-center space-x-4">
                                <h2 className="text-xl font-semibold text-gray-900">Welcome, {username}</h2>
                                <button
                                    onClick={() => {
                                        setShowProfile(true);
                                        fetchProfile();
                                    }}
                                    className="p-2 rounded-full bg-green-100 text-green-600 hover:bg-green-200 transition-colors duration-200"
                                    title="View Profile"
                                >
                                    <User size={20} />
                                </button>
                                <button
                                    onClick={handleLogout}
                                    className="px-4 py-2 rounded-md bg-red-500 text-white text-sm font-medium hover:bg-red-600 transition-colors duration-200"
                                >
                                    Logout
                                </button>
                            </div>
                        </div>
                    </div>
                </header>

                {/* Message Display */}
                {message && (
                    <div className={`mx-4 mt-4 p-3 rounded-lg flex items-center justify-between ${
                        messageType === 'success' ? 'bg-green-100 text-green-700' :
                        messageType === 'error' ? 'bg-red-100 text-red-700' :
                        'bg-yellow-100 text-yellow-700'
                    } transition-opacity duration-300`}>
                        <span>{message}</span>
                        <button onClick={() => setMessage('')} className="ml-4 text-lg p-1 rounded-full hover:bg-opacity-20 hover:bg-current">
                            <X size={20} />
                        </button>
                    </div>
                )}

                {/* Content based on active tab */}
                <div className="flex-1 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 w-full">
                    {/* Crops Tab */}
                    {activeTab === 'crops' && (
                        <div>
                            <h2 className="text-2xl font-bold text-gray-900 mb-6">Available Crops</h2>

                            {/* Search and Sort Controls */}
                            <div className="flex flex-col sm:flex-row justify-between items-center mb-6 space-y-4 sm:space-y-0 sm:space-x-4">
                                {/* Search Input */}
                                <div className="w-full sm:w-1/2">
                                    <input
                                        type="text"
                                        placeholder="Search by crop name..."
                                        className="w-full p-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-green-500"
                                        value={searchTerm}
                                        onChange={handleSearchChange}
                                    />
                                </div>

                                {/* Sort Dropdown */}
                                <div className="flex items-center space-x-2 w-full sm:w-auto">
                                    <label htmlFor="sort-by" className="text-gray-700 font-medium">Sort by:</label>
                                    <select
                                        id="sort-by"
                                        className="p-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-green-500"
                                        onChange={(e) => requestSort(e.target.value)}
                                        value={sortConfig.key || ''}
                                    >
                                        <option value="">None</option>
                                        
                                        <option value="cropName">Crop Name</option>
                                        <option value="cropPrice">Price</option>
                                        <option value="cropQty">Quantity</option>
                                    </select>
                                    {sortConfig.key && (
                                        <button
                                            onClick={() => requestSort(sortConfig.key)} // Toggle direction for current key
                                            className="p-2 rounded-md bg-gray-200 hover:bg-gray-300 text-gray-700 transition-colors duration-200"
                                            title={`Sort ${sortConfig.direction === 'ascending' ? 'Descending' : 'Ascending'}`}
                                        >
                                            {sortConfig.direction === 'ascending' ? '▲' : '▼'}
                                        </button>
                                    )}
                                </div>
                            </div>

                            {/* Crops Grid */}
                            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                                {loading ? (
                                    <p className="col-span-full text-center text-gray-600">Loading crops...</p>
                                ) : processedCrops.length > 0 ? (
                                    processedCrops.map((crop) => (
                                        <div key={crop.id} className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow duration-200">
                                            <img
                                                src={crop.imageUrl || `https://placehold.co/400x200/edf2f7/4a5568?text=${crop.cropName.replace(/\s/g, '+')}`}
                                                alt={crop.cropName}
                                                className="w-full h-48 object-cover"
                                                onError={(e) => { e.target.onerror = null; e.target.src = `https://placehold.co/400x200/edf2f7/4a5568?text=${crop.cropName.replace(/\s/g, '+')}`; }}
                                            />
                                            <div className="p-4">
                                                <h3 className="text-xl font-semibold text-gray-900 mb-1">{crop.cropName}</h3>
                                                <p className="text-sm text-gray-600 mb-2">{crop.cropType}</p>
                                                <p className="text-gray-700 text-sm mb-3">{crop.cropDescription}</p>
                                                <div className="flex justify-between items-center mb-3">
                                                    <span className="text-green-600 font-bold text-lg">₹{crop.cropPrice}/kg</span>
                                                    <span className="text-gray-800 text-md font-medium">{crop.cropQty} kg available</span>
                                                </div>
                                                <div className="text-xs text-gray-500 mb-4 space-y-1">
                                                    <p className="flex items-center"><User size={14} className="mr-1 text-gray-400" /> Farmer: {crop.farmerName || 'N/A'}</p>
                                                    <p className="flex items-center"><Phone size={14} className="mr-1 text-gray-400" /> Mobile: {crop.farmerMobile || 'N/A'}</p>
                                                    <p className="flex items-center"><MapPin size={14} className="mr-1 text-gray-400" /> Address: {crop.farmerAddress || 'N/A'}</p>
                                                </div>
                                                <div className="flex items-center justify-between mt-4">
                                                    <div className="flex items-center space-x-2">
                                                        <button
                                                            onClick={() => handleCropQuantityChange(crop.id, -1)}
                                                            className="p-1 rounded-full bg-gray-200 text-gray-700 hover:bg-gray-300 disabled:opacity-50 transition-colors duration-200"
                                                            disabled={(cropQuantities[crop.id] || 1) <= 1}
                                                        >
                                                            <MinusCircle size={20} />
                                                        </button>
                                                        <span className="font-semibold text-lg w-8 text-center">
                                                            {cropQuantities[crop.id] || 1}
                                                        </span>
                                                        <button
                                                            onClick={() => handleCropQuantityChange(crop.id, 1)}
                                                            className="p-1 rounded-full bg-gray-200 text-gray-700 hover:bg-gray-300 transition-colors duration-200"
                                                            disabled={(cropQuantities[crop.id] || 1) >= crop.cropQty}
                                                        >
                                                            <PlusCircle size={20} />
                                                        </button>
                                                    </div>
                                                    <button
                                                        onClick={() => handleAddToCart(crop.id)}
                                                        className="px-4 py-2 rounded-md bg-green-500 text-white font-medium hover:bg-green-600 flex items-center transition-colors duration-200"
                                                        disabled={crop.cropQty <= 0} // Disable if out of stock
                                                    >
                                                        <ShoppingCart size={16} className="mr-2" />
                                                        {crop.cropQty <= 0 ? 'Out of Stock' : 'Add to Cart'}
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                    ))
                                ) : (
                                    <p className="col-span-full text-center text-gray-600">No crops found matching your criteria.</p>
                                )}
                            </div>

                            {/* Pagination Controls */}
                            {totalPages > 1 && (
                                <div className="flex justify-center items-center space-x-2 mt-8">
                                    <button
                                        onClick={handlePrevPage}
                                        disabled={currentPage === 1}
                                        className="px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300 disabled:opacity-50 transition-colors duration-200"
                                    >
                                        Previous
                                    </button>
                                    {[...Array(totalPages)].map((_, index) => (
                                        <button
                                            key={index + 1}
                                            onClick={() => handlePageChange(index + 1)}
                                            className={`px-4 py-2 rounded-md ${
                                                currentPage === index + 1 ? 'bg-green-600 text-white' : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                                            } transition-colors duration-200`}
                                        >
                                            {index + 1}
                                        </button>
                                    ))}
                                    <button
                                        onClick={handleNextPage}
                                        disabled={currentPage === totalPages}
                                        className="px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300 disabled:opacity-50 transition-colors duration-200"
                                    >
                                        Next
                                    </button>
                                </div>
                            )}
                        </div>
                    )}

                    {/* Cart Tab */}
                    {activeTab === 'cart' && (
                        <div>
                            <h2 className="text-2xl font-bold text-gray-900 mb-6">My Cart</h2>
                            {loading ? (
                                <p className="text-center text-gray-600">Loading cart...</p>
                            ) : cartItems.length > 0 ? (
                                <div className="bg-white rounded-lg shadow-md p-6">
                                    <ul className="divide-y divide-gray-200">
                                        {cartItems.map(item => (
                                            <li key={item.cartItemId} className="py-4 flex items-center justify-between">
                                                <div className="flex items-center space-x-4">
                                                    <img
                                                        src={item.imageUrl || `https://placehold.co/64x64/edf2f7/4a5568?text=${item.cropName.replace(/\s/g, '+')}`}
                                                        alt={item.cropName}
                                                        className="w-16 h-16 object-cover rounded-md"
                                                        onError={(e) => { e.target.onerror = null; e.target.src = `https://placehold.co/64x64/edf2f7/4a5568?text=${item.cropName.replace(/\s/g, '+')}`; }}
                                                    />
                                                    <div>
                                                        <p className="text-lg font-medium text-gray-900">{item.cropName}</p>
                                                        <p className="text-sm text-gray-500">
                                                            {item.quantity} kg x ₹{item.cropPrice}/kg
                                                        </p>
                                                    </div>
                                                </div>
                                                <div className="flex items-center space-x-4">
                                                    <span className="text-lg font-bold text-green-700">
                                                        ₹{item.totalPrice ? item.totalPrice.toFixed(2) : '0.00'}
                                                    </span>
                                                    {/* You can add a remove item button here if the API supports it */}
                                                    {/* <button className="p-1 rounded-full text-red-500 hover:bg-red-100 transition-colors duration-200">
                                                        <Trash2 size={20} />
                                                    </button> */}
                                                </div>
                                            </li>
                                        ))}
                                    </ul>
                                    <div className="mt-6 pt-4 border-t border-gray-200 flex justify-between items-center">
                                        <span className="text-xl font-bold text-gray-900">Total:</span>
                                        <span className="text-2xl font-bold text-green-800">
                                            ₹{cartItems.reduce((sum, item) => sum + (item.totalPrice || 0), 0).toFixed(2)}
                                        </span>
                                    </div>
                                    <div className="mt-6 flex justify-end space-x-4">
                                        <button
                                            onClick={handleClearCart}
                                            className="px-6 py-2 rounded-md border border-gray-300 text-gray-700 font-medium hover:bg-gray-100 flex items-center transition-colors duration-200"
                                        >
                                            <Trash2 size={18} className="mr-2" />
                                            Clear Cart
                                        </button>
                                        <button
                                            onClick={handleCheckout} // This now directly initiates Stripe redirect
                                            className="px-6 py-2 rounded-md bg-green-600 text-white font-medium hover:bg-green-700 flex items-center transition-colors duration-200"
                                        >
                                            <CreditCard size={18} className="mr-2" />
                                            Proceed to Checkout
                                        </button>
                                    </div>
                                </div>
                            ) : (
                                <p className="text-center text-gray-600">Your cart is empty.</p>
                            )}
                        </div>
                    )}

                    {/* Orders Tab */}
                    {activeTab === 'orders' && (
                        <div>
                            <h2 className="text-2xl font-bold text-gray-900 mb-6">My Orders</h2>
                            {loading ? (
                                <p className="text-center text-gray-600">Loading orders...</p>
                            ) : orders.length > 0 ? (
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                    {orders.map(order => (
                                        <div key={order.orderID} className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow duration-200">
                                            <div className="flex justify-between items-center mb-4">
                                                <h3 className="text-lg font-semibold text-gray-900">Order #{order.orderID?.slice(0, 8) || 'N/A'}</h3>
                                                <span className={`px-3 py-1 rounded-full text-sm font-medium ${
                                                    order.orderStatus === 'Out for delivery' ? 'bg-yellow-100 text-yellow-800 flex items-center' :
                                                    order.orderStatus === 'DELIVERED' ? 'bg-green-100 text-green-800 flex items-center' :
                                                    'bg-red-100 text-red-800 flex items-center'
                                                }`}>
                                                    {order.orderStatus === 'Out for delivery' ? <Clock size={16} className="mr-1" /> : <CheckCircle size={16} className="mr-1" />}
                                                    {order.orderStatus}
                                                </span>
                                            </div>
                                            <p className="text-gray-700 mb-2"><span className="font-medium">Crop:</span> {order.cropName || 'N/A'}</p>
                                            <p className="text-gray-700 mb-2"><span className="font-medium">Quantity:</span> {order.quantity} kg</p>
                                            <p className="text-gray-700 mb-2"><span className="font-medium">Total Price:</span> ₹{order.totalPrice ? order.totalPrice.toFixed(2) : '0.00'}</p>
                                            <p className="text-sm text-gray-500 flex items-center mt-3"><User size={14} className="mr-1 text-gray-400" /> Farmer: {order.farmerName || 'N/A'}</p>
                                            <p className="text-sm text-gray-500 flex items-center"><Phone size={14} className="mr-1 text-gray-400" /> Mobile: {order.farmerMobile || 'N/A'}</p>
                                            <p className="text-sm text-gray-500 flex items-center"><MapPin size={14} className="mr-1 text-gray-400" /> Address: {order.farmerAddress || 'N/A'}</p>

                                            {/* NEW: Mark as Delivered Button */}
                                            {order.orderStatus === 'Out for delivery' && (
                                                <button
                                                    onClick={() => handleMarkAsDeliveredClick(order)}
                                                    className="mt-4 w-full bg-blue-600 text-white py-2 rounded-md hover:bg-blue-700 flex items-center justify-center transition-colors duration-200"
                                                    disabled={!profile || !profile.user || !profile.user.username} // Disable if email for OTP is missing
                                                >
                                                    <CheckCircle size={16} className="mr-2" />
                                                    Mark as Delivered
                                                </button>
                                            )}
                                        </div>
                                    ))}
                                </div>
                            ) : (
                                <p className="text-center text-gray-600">You have no orders yet.</p>
                            )}
                        </div>
                    )}
                </div>
            </main>

            {/* Profile Overlay */}
            {showProfile && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
                    <div className="bg-white p-8 rounded-lg shadow-xl w-full max-w-md relative animate-fade-in-up">
                        <button onClick={() => setShowProfile(false)} className="absolute top-4 right-4 text-gray-500 hover:text-gray-700 transition-colors duration-200">
                            <X size={24} />
                        </button>
                        <h2 className="text-2xl font-bold text-gray-800 mb-6 border-b pb-3">My Profile</h2>
                        {loading ? (
                            <p className="text-center text-gray-600">Loading profile...</p>
                        ) : profile ? (
                            <div className="space-y-4 text-gray-800">
                                <p><span className="font-semibold">Username:</span> {profile.user.username}</p>
                                <p><span className="font-semibold">Role:</span> {profile.user.role}</p>
                                <p><span className="font-semibold">Address:</span> {profile.user.address || 'N/A'}</p>
                                <p><span className="font-semibold">Mobile:</span> {profile.user.mobileNumber || 'N/A'}</p>
                                <div className="mt-6 flex justify-end">
                                    <button
                                        onClick={() => setShowUpdateProfile(true)}
                                        className="px-5 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600 transition-colors duration-200"
                                    >
                                        Update Profile
                                    </button>
                                </div>
                            </div>
                        ) : (
                            <p className="text-center text-gray-600">Profile data not available.</p>
                        )}
                    </div>
                </div>
            )}

            {/* Update Profile Overlay */}
            {showUpdateProfile && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
                    <div className="bg-white p-8 rounded-lg shadow-xl w-full max-w-md relative animate-fade-in-up">
                        <button onClick={() => setShowUpdateProfile(false)} className="absolute top-4 right-4 text-gray-500 hover:text-gray-700 transition-colors duration-200">
                            <X size={24} />
                        </button>
                        <h2 className="text-2xl font-bold text-gray-800 mb-6 border-b pb-3">Update Profile</h2>
                        <div className="space-y-5">
                            <div>
                                <label htmlFor="address" className="block text-sm font-medium text-gray-700 mb-1">Address</label>
                                <input
                                    type="text"
                                    id="address"
                                    name="address"
                                    value={profileUpdate.address}
                                    onChange={(e) => setProfileUpdate({ ...profileUpdate, address: e.target.value })}
                                    className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-green-500 focus:border-green-500 outline-none"
                                />
                            </div>
                            <div>
                                <label htmlFor="mobileNumber" className="block text-sm font-medium text-gray-700 mb-1">Mobile Number</label>
                                <input
                                    type="text"
                                    id="mobileNumber"
                                    name="mobileNumber"
                                    value={profileUpdate.mobileNumber}
                                    onChange={(e) => setProfileUpdate({ ...profileUpdate, mobileNumber: e.target.value })}
                                    className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-green-500 focus:border-green-500 outline-none"
                                />
                            </div>
                            <div className="mt-6 flex justify-end space-x-3">
                                <button
                                    onClick={() => setShowUpdateProfile(false)}
                                    className="px-5 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-100 transition-colors duration-200"
                                >
                                    Cancel
                                </button>
                                <button
                                    onClick={handleUpdateProfile}
                                    className="px-5 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition-colors duration-200"
                                    disabled={loading}
                                >
                                    {loading ? 'Saving...' : 'Save Changes'}
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}

            {/* Chatbot Overlay */}
            {showChatbot && (
                <div className="fixed bottom-4 right-4 bg-white rounded-lg shadow-xl border border-gray-200 w-80 h-96 flex flex-col z-50 animate-fade-in-up">
                    <div className="flex justify-between items-center p-3 border-b border-gray-200 bg-green-500 text-white rounded-t-lg">
                        <h3 className="font-semibold">CropBot</h3>
                        <button onClick={() => setShowChatbot(false)} className="text-white hover:text-gray-100 p-1 rounded-full hover:bg-opacity-20 hover:bg-current">
                            <X size={20} />
                        </button>
                    </div>
                    <div className="flex-1 p-3 overflow-y-auto space-y-3 custom-scrollbar">
                        {chatMessages.map((msg) => (
                            <div
                                key={msg.id}
                                className={`flex ${msg.type === 'user' ? 'justify-end' : 'justify-start'}`}
                            >
                                <div
                                    className={`p-2 rounded-lg max-w-[75%] break-words ${
                                        msg.type === 'user'
                                            ? 'bg-blue-500 text-white'
                                            : 'bg-gray-200 text-gray-800'
                                    }`}
                                >
                                    {msg.text}
                                </div>
                            </div>
                        ))}
                        {chatbotLoading && (
                            <div className="flex justify-start">
                                <div className="p-2 rounded-lg bg-gray-200 text-gray-800 animate-pulse">Thinking...</div>
                            </div>
                        )}
                    </div>
                    <form onSubmit={handleChatSendMessage} className="p-3 border-t border-gray-200 flex">
                        <input
                            type="text"
                            value={chatInput}
                            onChange={(e) => setChatInput(e.target.value)}
                            placeholder="Type your message..."
                            className="flex-1 border border-gray-300 rounded-l-md px-3 py-2 focus:outline-none focus:ring-green-500 focus:border-green-500"
                            disabled={chatbotLoading}
                        />
                        <button
                            type="submit"
                            className="bg-green-500 text-white p-2 rounded-r-md hover:bg-green-600 flex items-center justify-center transition-colors duration-200"
                            disabled={chatbotLoading}
                        >
                            <Send size={20} />
                        </button>
                    </form>
                </div>
            )}

            {/* Chatbot Toggle Button */}
            <button
                onClick={() => setShowChatbot(!showChatbot)}
                className="fixed bottom-4 right-4 bg-green-500 text-white p-4 rounded-full shadow-lg hover:bg-green-600 focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-offset-2 z-40 transition-all duration-300 transform hover:scale-105"
                aria-label="Toggle Chatbot"
            >
                <MessageCircle size={28} />
            </button>

            {/* NEW: OTP Verification Modal */}
            <EmailOtpVerificationModal
                show={showOtpModal}
                onClose={() => setShowOtpModal(false)}
                onVerified={handleOtpVerified} // This callback handles post-verification logic
                token={token}
                showTimedMessage={showTimedMessage}
                initialEmail={otpEmail} // Pass the dynamically set email
                triggerContext={otpContext} // Pass the context ('payment' or 'delivery')
            />
        </div>
    );
};

export default DealerDashboard;
