import React, { useState, useEffect } from 'react'; // Import useEffect
import { Eye, EyeOff, User, Lock, Phone, MapPin, UserCheck } from 'lucide-react';

const AuthPage = () => {
  const [isSignUp, setIsSignUp] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState('');
  const [showWelcome, setShowWelcome] = useState(true);
  const [usernameExists, setUsernameExists] = useState(false);
  const [usernameError, setUsernameError] = useState('');

  const [signUpData, setSignUpData] = useState({
    username: '',
    mobileNumber: '',
    address: '',
    role: 'FARMER',
    password: ''
  });

  const [signInData, setSignInData] = useState({
    username: '',
    password: ''
  });

  // Effect to clear messages after a delay
  useEffect(() => {
    if (message) {
      const timer = setTimeout(() => {
        setMessage('');
        setMessageType('');
      }, 3000); // Message will disappear after 3 seconds
      return () => clearTimeout(timer); // Clean up the timer
    }
  }, [message]);

  const handleSignUpChange = async (e) => {
    const { name, value } = e.target;

    setSignUpData((prev) => ({
      ...prev,
      [name]: value,
    }));

    if (name === "username") {
      setUsernameError("");
      setUsernameExists(false); // Reset usernameExists when username changes

      // Optional: prevent calling API for short usernames
      if (value.length >= 4) {
        try {
          const res = await fetch(`http://localhost:8888/api/auth/username-exists?username=${value}`);
          const exists = await res.json(); // should be true/false directly from your API
          if (exists === true) {
            setUsernameExists(true);
            setUsernameError("Username already exists. Try something else.");
          } else {
            setUsernameExists(false);
          }
        } catch (err) {
          console.error("Error checking username:", err);
          // setUsernameError("Error checking username availability."); // This line is causing the message
        }
      }
    }
  };

  const handleSignInChange = (e) => {
    setSignInData({
      ...signInData,
      [e.target.name]: e.target.value
    });
  };

  const handleSignUp = async (e) => {
    e.preventDefault(); // Prevent default form submission

    // Prevent signup if username already exists
    if (usernameExists) {
      setMessage('Cannot register: Username already exists.');
      setMessageType('error');
      return; // Stop the function execution
    }

    setLoading(true);
    setMessage('');

    try {
      const response = await fetch('http://localhost:8888/api/auth/signup', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          ...signUpData,
          active: true
        }),
      });

      if (response.ok) {
        const result = await response.text();
        setMessage(result);
        setMessageType('success');
        setSignUpData({
          username: '',
          mobileNumber: '',
          address: '',
          role: 'FARMER',
          password: ''
        });
        setTimeout(() => setIsSignUp(false), 2000);
      } else {
        // More specific error handling for signup if possible from backend
        const errorData = await response.text(); // Or response.json() if your backend sends JSON
        setMessage(errorData || 'Registration failed. Please try again.');
        setMessageType('error');
      }
    } catch (error) {
      setMessage('Network error. Please check your connection.');
      setMessageType('error');
    } finally {
      setLoading(false);
    }
  };

  const handleSignIn = async (e) => {
    e.preventDefault(); // Prevent default form submission
    setLoading(true);
    setMessage('');

    try {
      const response = await fetch('http://localhost:8888/api/auth/signin', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(signInData),
      });

      if (response.ok) {
        const token = await response.text();
        localStorage.setItem('token', token);

        // Validate token and get user details
        const validateResponse = await fetch('http://localhost:8888/api/auth/validate', {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        });

        if (validateResponse.ok) {
          const userData = await validateResponse.json();
          localStorage.setItem('userRole', userData.role);
          localStorage.setItem('userId', userData.id);
          localStorage.setItem('username', userData.username);
          localStorage.setItem('redirectUrl', userData.redirectUrl);

          setMessage(`Welcome ${userData.username}! Redirecting to ${userData.role} dashboard...`);
          setMessageType('success');

          // Redirect based on role
          setTimeout(() => {
            switch (userData.role) {
              case 'ADMIN':
                window.location.href = '/admin-dashboard';
                break;
              case 'DEALER':
                window.location.href = '/dealer-dashboard';
                break;
              case 'FARMER':
                window.location.href = '/farmer-dashboard';
                break;
              default:
                window.location.href = '/dashboard';
            }
          }, 2000);
        } else {
          setMessage('Authentication failed. Please try again.');
          setMessageType('error');
        }
      } else if (response.status === 403) {
        const errorData = await response.json();
        setMessage(errorData.message || 'Access forbidden.');
        setMessageType('error');
      } else {
        setMessage('Invalid credentials. Please try again.');
        setMessageType('error');
      }
    } catch (error) {
      setMessage('Admin deactivated your account for suspicious activity');
      setMessageType('error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      {/* Welcome Page */}
      {showWelcome && (
        <div className="min-h-screen flex flex-col md:flex-row bg-gradient-to-br from-green-100 via-emerald-100 to-emerald-200 font-sans overflow-hidden">
          {/* Image Section */}
          <div className="relative md:w-1/2 h-72 md:h-auto">
            <img
              src="https://img.freepik.com/premium-photo/farmer-using-smart-farming-application-digital-tablet-managing-crops-production-growth_308072-5058.jpg?w=2000"
              alt="Farmer using smart farming app"
              className="absolute inset-0 w-full h-full object-cover"
            />
            {/* Overlay */}
            <div className="absolute inset-0 bg-emerald-900/40 backdrop-brightness-75"></div>
          </div>

          {/* Content Section */}
          <div className="md:w-1/2 flex items-center justify-center p-6 md:p-12 relative z-10">
            <div className="bg-white/80 backdrop-blur-lg rounded-3xl shadow-2xl p-8 md:p-10 text-center animate-fade-in-up w-full max-w-md">
              <h1 className="text-4xl font-extrabold text-green-800 mb-4">🌾 Welcome to Crop Deal</h1>
              <p className="text-gray-700 mb-6 text-base leading-relaxed">
                Your all-in-one platform connecting farmers and dealers. Simplify your crop transactions, manage orders, and grow your agri-business seamlessly.
              </p>
              <button
                onClick={() => setShowWelcome(false)}
                className="w-full bg-green-600 hover:bg-green-700 text-white py-3 px-6 rounded-lg font-semibold transition-all duration-200 shadow-md hover:shadow-lg focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-offset-2"
              >
                Continue to Sign In
              </button>
            </div>
          </div>
        </div>

      )}
      {!showWelcome && (
        <div className="min-h-screen bg-gradient-to-br from-green-100 via-emerald-100 to-emerald-200 flex items-center justify-center p-4">
          <div className="w-full max-w-md bg-white/80 backdrop-blur-lg rounded-3xl shadow-2xl p-8 animate-fade-in-up">
            {/* Header */}
            <div className="text-center mb-8">
              <h1 className="text-4xl font-extrabold text-green-800 mb-2">🌾 Crop Deal</h1>
              <p className="text-gray-700 text-sm">Connect. Trade. Grow.</p>
            </div>

            {/* Toggle Buttons */}
            <div className="flex bg-gray-100 rounded-lg p-1 mb-6 shadow-inner">
              <button
                onClick={() => setIsSignUp(false)}
                className={`flex-1 py-2 px-4 rounded-md text-sm font-medium transition-all duration-200 ${
                  !isSignUp
                    ? 'bg-white text-green-700 shadow'
                    : 'text-gray-600 hover:text-gray-800'
                }`}
              >
                Sign In
              </button>
              <button
                onClick={() => setIsSignUp(true)}
                className={`flex-1 py-2 px-4 rounded-md text-sm font-medium transition-all duration-200 ${
                  isSignUp
                    ? 'bg-white text-green-700 shadow'
                    : 'text-gray-600 hover:text-gray-800'
                }`}
              >
                Sign Up
              </button>
            </div>

            {/* Flash Message */}
            {message && (
              <div className={`mb-4 p-3 rounded-lg text-sm border ${
                messageType === 'success'
                  ? 'bg-green-100 text-green-700 border-green-200'
                  : 'bg-red-100 text-red-700 border-red-200'
              }`}>
                {message}
              </div>
            )}
            {/* Sign In Form */}
            {!isSignUp && (
              <form onSubmit={handleSignIn} className="space-y-6"> {/* Added form and onSubmit */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Username
                  </label>
                  <div className="relative">
                    <User className="absolute left-3 top-3 h-5 w-5 text-gray-400" />
                    <input
                      type="text"
                      name="username"
                      value={signInData.username}
                      onChange={handleSignInChange}
                      className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                      placeholder="Enter your username"
                      required
                    />
                  </div>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Password
                  </label>
                  <div className="relative">
                    <Lock className="absolute left-3 top-3 h-5 w-5 text-gray-400" />
                    <input
                      type={showPassword ? 'text' : 'password'}
                      name="password"
                      value={signInData.password}
                      onChange={handleSignInChange}
                      className="w-full pl-10 pr-12 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                      placeholder="Enter your password"
                      required
                    />
                    <button
                      type="button" // Important: type="button" to prevent this button from submitting the form
                      onClick={() => setShowPassword(!showPassword)}
                      className="absolute right-3 top-3 h-5 w-5 text-gray-400 hover:text-gray-600"
                    >
                      {showPassword ? <EyeOff /> : <Eye />}
                    </button>
                  </div>
                </div>

                <button
                  type="submit" // Changed to type="submit"
                  disabled={loading}
                  className="w-full bg-green-600 text-white py-3 px-4 rounded-lg font-medium hover:bg-green-700 focus:ring-2 focus:ring-green-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                >
                  {loading ? 'Signing In...' : 'Sign In'}
                </button>
                <p className="text-center mt-4">
                  Don't have an account?{' '}
                  <button
                    type="button" // Important: type="button" to prevent this button from submitting the form
                    onClick={() => setIsSignUp(true)}
                    className="text-green-600 hover:text-green-800 font-medium"
                  >
                    Register
                  </button>
                </p>
              </form>
            )}

            {/* Sign Up Form */}
            {isSignUp && (
              <form onSubmit={handleSignUp} className="space-y-6"> {/* Added form and onSubmit */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Username
                  </label>
                  <div className="relative">
                    <User className="absolute left-3 top-3 h-5 w-5 text-gray-400" />
                    <input
                      type="text"
                      name="username"
                      value={signUpData.username}
                      onChange={handleSignUpChange}
                      className={`w-full pl-10 pr-4 py-3 border ${
                        usernameExists ? 'border-red-500' : 'border-gray-300'
                      } rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent`}
                      placeholder="Enter your username"
                      pattern="^[A-Za-z][A-Za-z0-9_]{2,14}$"
                      required
                      onInvalid={(e) =>
                        e.target.setCustomValidity(
                          usernameError ||
                          "Username must be 3–15 characters, start with a letter, and contain only letters, numbers, or underscores."
                        )
                      }
                      onInput={(e) => e.target.setCustomValidity("")}
                    />
                  </div>
                  {usernameError && (
                    <p className="text-sm text-red-600 mt-1">{usernameError}</p>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Mobile Number
                  </label>
                  <div className="relative">
                    <Phone className="absolute left-3 top-3 h-5 w-5 text-gray-400" />
                    <input
                      type="tel"
                      name="mobileNumber"
                      value={signUpData.mobileNumber}
                      onChange={handleSignUpChange}
                      className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                      placeholder="Enter 10 digit phone number"
                      pattern="^[6-9][0-9]{9}$"
                      maxLength={10}
                      minLength={10}
                      required
                      onInvalid={(e) => e.target.setCustomValidity("Enter a valid 10-digit mobile number starting with 6, 7, 8, or 9")}
                      onInput={(e) => e.target.setCustomValidity("")}
                    />
                  </div>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Address
                  </label>
                  <div className="relative">
                    <MapPin className="absolute left-3 top-3 h-5 w-5 text-gray-400" />
                    <input
                      type="text"
                      name="address"
                      value={signUpData.address}
                      onChange={handleSignUpChange}
                      className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                      placeholder="Enter your address"
                      pattern="[A-Za-z]{3,100}"
                      required
                       onInvalid={(e) => e.target.setCustomValidity("Enter a valid address")}
                      onInput={(e) => e.target.setCustomValidity("")}
                    
                    />
                  </div>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Role
                  </label>
                  <div className="relative">
                    <UserCheck className="absolute left-3 top-3 h-5 w-5 text-gray-400" />
                    <select
                      name="role"
                      value={signUpData.role}
                      onChange={handleSignUpChange}
                      className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent appearance-none bg-white"
                      required
                    >
                      <option value="FARMER">Farmer</option>
                      <option value="DEALER">Dealer</option>

                    </select>
                  </div>
                </div>

                <div>
  <label className="block text-sm font-medium text-gray-700 mb-2">
    Password
  </label>
  <div className="relative">
    <Lock className="absolute left-3 top-3 h-5 w-5 text-gray-400" />
    <input
      type={showPassword ? 'text' : 'password'}
      name="password"
      value={signUpData.password}
      onChange={handleSignUpChange}
      className="w-full pl-10 pr-12 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
      placeholder="Enter your password"
      minLength={5}
      required
      onInvalid={(e) =>
        e.target.setCustomValidity("Password must be at least 5 characters")
      }
      onInput={(e) => e.target.setCustomValidity("")}
    />
    <button
      type="button"
      onClick={() => setShowPassword(!showPassword)}
      className="absolute right-3 top-3 h-5 w-5 text-gray-400 hover:text-gray-600"
    >
      {showPassword ? <EyeOff /> : <Eye />}
    </button>
  </div>
</div>

                <button
                  type="submit"
                  disabled={loading || usernameExists}
                  className="w-full bg-green-600 text-white py-3 px-4 rounded-lg font-medium hover:bg-green-700 focus:ring-2 focus:ring-green-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                >
                  {loading ? 'Creating Account...' : 'Create Account'}
                </button>

              </form>
            )}
          </div>
        </div>
      )}
    </>
  );
};

export default AuthPage;