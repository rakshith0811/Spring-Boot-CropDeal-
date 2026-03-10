import React, { useState, useEffect, useCallback } from 'react'; // Import useCallback
import { User, Users, Shield, Package, Truck, Edit, ToggleLeft, ToggleRight, X } from 'lucide-react';

const AdminDashboard = () => {
  const [activeTab, setActiveTab] = useState('welcome'); // Default to profile tab
  const [showProfileModal, setShowProfileModal] = useState(false);
  const [showUpdateProfileModal, setShowUpdateUpdateProfileModal] = useState(false);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState('');

  // Admin data from localStorage
  const [adminId] = useState(localStorage.getItem('userId'));
  const [token] = useState(localStorage.getItem('token'));
  const [username] = useState(localStorage.getItem('username'));

  // Profile data
  const [adminProfile, setAdminProfile] = useState(null);
  const [profileUpdate, setProfileUpdate] = useState({
    address: '',
    mobileNumber: '',
    active: true // Assuming admin status can also be updated from their own profile
  });

  // Data for various lists
  const [admins, setAdmins] = useState([]);
  const [dealers, setDealers] = useState([]);
  const [farmers, setFarmers] = useState([]);
  const [orders, setOrders] = useState([]);

  const handleLogout = () => {
  // Clear user data from localStorage
  localStorage.removeItem('token');
  localStorage.removeItem('userId');
  localStorage.removeItem('username'); // Assuming you also store username

  // Redirect to the login page
  // You would typically use react-router-dom's history.push or navigate here.
  // For simplicity, a direct window.location.href is shown, but a router is preferred in production apps.
  window.location.href = '/'; // Or whatever your login route is
};

  // Fetch Admin Profile - Wrapped in useCallback
  const fetchAdminProfile = useCallback(async () => {
    try {
      const response = await fetch(`http://localhost:8888/api/admin/profile/${adminId}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (response.ok) {
        const data = await response.json();
        setAdminProfile(data);
        setProfileUpdate({
          address: data.address,
          mobileNumber: data.mobileNumber,
          active: data.active
        });
      } else {
        setMessage('Failed to fetch admin profile.');
        setMessageType('error');
      }
    } catch (error) {
      console.error('Error fetching admin profile:', error);
      setMessage('Error fetching admin profile.');
      setMessageType('error');
    }
  }, [adminId, token, setMessage, setMessageType]); // Add dependencies

  // Update Admin Profile
  const handleUpdateAdminProfile = async () => {
    setLoading(true);
    try {
      const response = await fetch(`http://localhost:8888/api/admin/profile/${adminId}`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(profileUpdate)
      });
      if (response.ok) {
        setMessage('Profile updated successfully!');
        setMessageType('success');
        setShowUpdateUpdateProfileModal(false); // Corrected typo here
        fetchAdminProfile(); // Refresh profile data
      } else {
        setMessage('Failed to update profile.');
        setMessageType('error');
      }
    } catch (error) {
      setMessage('Error updating profile');
      setMessageType('error');
    } finally {
      setLoading(false);
    }
  };

  // Fetch All Admins - Wrapped in useCallback
  const fetchAllAdmins = useCallback(async () => {
    try {
      const response = await fetch('http://localhost:8888/api/admin/admins', {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (response.ok) {
        const data = await response.json();
        setAdmins(data);
      } else {
        setMessage('Failed to fetch admins.');
        setMessageType('error');
      }
    } catch (error) {
      console.error('Error fetching admins:', error);
      setMessage('Error fetching admins.');
      setMessageType('error');
    }
  }, [token, setMessage, setMessageType]); // Add dependencies

  // Fetch All Dealers - Wrapped in useCallback
  const fetchAllDealers = useCallback(async () => {
    try {
      const response = await fetch('http://localhost:8888/api/admin/dealers', {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (response.ok) {
        const data = await response.json();
        setDealers(data);
      } else {
        setMessage('Failed to fetch dealers.');
        setMessageType('error');
      }
    } catch (error) {
      console.error('Error fetching dealers:', error);
      setMessage('Error fetching dealers.');
      setMessageType('error');
    }
  }, [token, setMessage, setMessageType]); // Add dependencies

  // Fetch All Farmers - Wrapped in useCallback
  const fetchAllFarmers = useCallback(async () => {
    try {
      const response = await fetch('http://localhost:8888/api/admin/farmers', {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (response.ok) {
        const data = await response.json();
        setFarmers(data);
      } else {
        setMessage('Failed to fetch farmers.');
        setMessageType('error');
      }
    } catch (error) {
      console.error('Error fetching farmers:', error);
      setMessage('Error fetching farmers.');
      setMessageType('error');
    }
  }, [token, setMessage, setMessageType]); // Add dependencies

  // Update User (Farmer/Dealer) Status
  const handleUpdateUserStatus = async (id, currentStatus, role) => {
    setLoading(true);
    try {
      const endpoint = role === 'FARMER' ?
        `http://localhost:8888/api/admin/farmers/status/${id}` :
        `http://localhost:8888/api/admin/dealers/status/${id}`;

      const response = await fetch(endpoint, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ active: !currentStatus }) // Toggle the status
      });

      if (response.ok) {
        setMessage(`${role} status updated successfully!`);
        setMessageType('success');
        // Refresh the relevant list
        if (role === 'FARMER') {
          fetchAllFarmers();
        } else {
          fetchAllDealers();
        }
      } else {
        setMessage(`Failed to update ${role} status.`);
        setMessageType('error');
      }
    } catch (error) {
      setMessage(`Error updating ${role} status.`);
      setMessageType('error');
    } finally {
      setLoading(false);
    }
  };

  // Fetch All Orders - Wrapped in useCallback
  const fetchAllOrders = useCallback(async () => {
    try {
      const response = await fetch('http://localhost:8888/api/orders/getAllOrder', {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (response.ok) {
        const data = await response.json();
        setOrders(data);
      } else {
        setMessage('Failed to fetch orders.');
        setMessageType('error');
      }
    } catch (error) {
      console.error('Error fetching orders:', error);
      setMessage('Error fetching orders.');
      setMessageType('error');
    }
  }, [token, setMessage, setMessageType]); // Add dependencies

  // Effect to fetch data based on active tab
  // Now includes the memoized fetch functions as dependencies
  useEffect(() => {
    if (activeTab === 'profile') fetchAdminProfile();
    if (activeTab === 'admins') fetchAllAdmins();
    if (activeTab === 'dealers') fetchAllDealers();
    if (activeTab === 'farmers') fetchAllFarmers();
    if (activeTab === 'orders') fetchAllOrders();
  }, [activeTab, fetchAdminProfile, fetchAllAdmins, fetchAllDealers, fetchAllFarmers, fetchAllOrders]);

  return (

    <div className="min-h-screen bg-gray-50 flex">
      {/* Side bar */}
              <aside className="w-64 bg-green-700 text-white shadow-md border-r border-green-800 p-4 flex flex-col">
            <div className="flex items-center mb-6 mt-2"> {/* Added mb-6 for spacing, mt-2 for top alignment */}
                <h1 className="text-4xl font-bold text-white">CropDeal</h1> {/* CropDeal added to sidebar */}
            </div>
            
            <hr/>
             <nav className="flex-1 space-y-2">
              <br/>
            
            <button
              onClick={() => setActiveTab('admins')}
              className={`w-full text-left py-2 px-3 rounded-md flex items-center text-sm font-medium ${
                        activeTab === 'admins'
                            ? 'bg-green-600 text-white'
                            : 'text-green-100 hover:bg-green-600 hover:text-white'
                    }`}
            >
              <Shield className="mr-3" size={30} />
              Manage Admins
            </button>
            <br/>
            <br/>
            <button
              onClick={() => setActiveTab('dealers')}
             className={`w-full text-left py-2 px-3 rounded-md flex items-center text-sm font-medium ${
                        activeTab === 'dealers'
                            ? 'bg-green-600 text-white'
                            : 'text-green-100 hover:bg-green-600 hover:text-white'
                    }`}
            >
              <Users className="mr-3" size={30} />
              Manage Dealers
            </button>
            <br/>
            <br/>
            <button
              onClick={() => setActiveTab('farmers')}
             className={`w-full text-left py-2 px-3 rounded-md flex items-center text-sm font-medium ${
                        activeTab === 'farmers'
                            ? 'bg-green-600 text-white'
                            : 'text-green-100 hover:bg-green-600 hover:text-white'
                    }`}
            >
              <Package className="mr-3" size={30} />
              Manage Farmers
            </button>
            <br/>
            <br/>
            <button
              onClick={() => setActiveTab('orders')}
              className={`w-full text-left py-2 px-3 rounded-md flex items-center text-sm font-medium ${
                        activeTab === 'orders'
                            ? 'bg-green-600 text-white'
                            : 'text-green-100 hover:bg-green-600 hover:text-white'
                    }`}
            >
              <Truck className="mr-3" size={30} />
              All Orders
            </button>
          </nav>
            <hr/>
            <br/>
</aside>
        <main className="flex-1 flex flex-col">
     <header className="bg-white shadow-sm border-b border-gray-200">
                <div className="max-w-full mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between items-center h-16">
                        <div className="flex items-center">
                            {/* Dealer Dashboard remains here */}
                            <h1 className="text-2xl font-bold text-green-800">Admin Dashboard</h1>
                        </div>
                        {/* Welcome message and Profile/Logout buttons */}
                        <div className="flex items-center space-x-4">
    <h2> <span className="text-xl font-semibold text-gray-900">Welcome, {username}</span></h2>
    <button
         onClick={() => {
            setShowProfileModal(true);
            fetchAdminProfile(); // Ensure latest profile is fetched
          }}
           className="p-2 rounded-full bg-green-100 text-green-600 hover:bg-green-200"
        title="View Profile"
    >
        <User size={20} />
    </button>
    <button
        onClick={handleLogout}
        className="px-4 py-2 rounded-md bg-red-500 text-white text-sm font-medium hover:bg-red-600"
    >
        Logout
    </button>
</div>
                    </div>
                </div>
            </header>

      {/* Message Display */}
      {message && (
        <div className={`mx-4 mt-4 p-3 rounded-lg ${
          messageType === 'success' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
        }`}>
          {message}
          <button onClick={() => setMessage('')} className="float-right">×</button>
        </div>
      )}

     
      {/* Main Content */}
      <div className="flex-1 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 w-full">
        {/* My Profile Tab (already handled by modal, but could also display here) */}
     {activeTab === 'welcome' && (
  <div className="p-10 bg-gradient-to-br from-emerald-50 to-green-100 rounded-xl shadow-xl w-full max-w-3xl mx-auto flex flex-col items-center gap-6">
    
    {/* Title */}
    <h1 className="text-4xl font-extrabold text-green-800 tracking-wide text-center">
      Welcome, Admin!
    </h1>

    {/* Image */}
    <img
      src="https://th.bing.com/th/id/OIP.23CQDu5sH3vdRigN0fQgTQHaDp?w=345&h=172&c=7&r=0&o=7&dpr=2&pid=1.7&rm=3"
      alt="Admin dashboard illustration"
      className="w-full max-w-md rounded-lg shadow-md object-cover"
    />

    {/* Description Text */}
    <p className="text-lg text-gray-700 leading-relaxed text-center max-w-xl">
      As an administrator, you have full control to manage users, monitor crop deals,
      and oversee platform settings. Ensure smooth operations and empower our community.
    </p>
  </div>
)}


        {activeTab === 'profile' && (
            <div className="bg-white rounded-lg shadow-md p-6">
                <h2 className="text-xl font-semibold text-gray-900 mb-6">My Profile</h2>
                {adminProfile ? (
                    <div className="space-y-3">
                        <p><strong>Username:</strong> {adminProfile.username}</p>
                        <p><strong>Role:</strong> {adminProfile.role}</p>
                        <p><strong>Mobile Number:</strong> {adminProfile.mobileNumber}</p>
                        <p><strong>Address:</strong> {adminProfile.address}</p>
                        <p><strong>Status:</strong> {adminProfile.active ? 'Active' : 'Inactive'}</p>
                        <button
                            onClick={() => {
                                setShowUpdateUpdateProfileModal(true); // Corrected typo here
                                setProfileUpdate({
                                    address: adminProfile.address,
                                    mobileNumber: adminProfile.mobileNumber,
                                    active: adminProfile.active
                                });
                            }}
                            className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 flex items-center mt-4"
                        >
                            <Edit size={16} className="mr-2" />
                            Update Profile
                        </button>
                    </div>
                ) : (
                    <p>Loading profile details...</p>
                )}
            </div>
        )}

        {/* Manage Admins Tab */}
        {activeTab === 'admins' && (
          <div>
            <h2 className="text-xl font-semibold text-gray-900 mb-6">Manage Admins</h2>
            <div className="overflow-x-auto">
              <table className="min-w-full bg-white shadow-md rounded-lg">
                <thead className="bg-gray-100">
                  <tr>
                    <th className="py-3 px-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                    <th className="py-3 px-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Username</th>
                    <th className="py-3 px-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Mobile Number</th>
                    <th className="py-3 px-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Address</th>
                    <th className="py-3 px-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200">
                  {admins.length > 0 ? (
                    admins.map((admin) => (
                      <tr key={admin.id}>
                        <td className="py-3 px-4 whitespace-nowrap text-sm text-gray-900">{admin.id}</td>
                        <td className="py-3 px-4 whitespace-nowrap text-sm text-gray-900">{admin.username || 'N/A'}</td>
                        <td className="py-3 px-4 whitespace-nowrap text-sm text-gray-900">{admin.mobileNumber || 'N/A'}</td>
                        <td className="py-3 px-4 whitespace-nowrap text-sm text-gray-900">{admin.address || 'N/A'}</td>
                        <td className="py-3 px-4 whitespace-nowrap text-sm">
                          <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                            admin.active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                          }`}>
                            {admin.active ? 'Active' : 'Inactive'}
                          </span>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan="5" className="py-4 px-4 text-center text-gray-500">No admins found.</td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* Manage Dealers Tab */}
        {activeTab === 'dealers' && (
          <div>
            <h2 className="text-xl font-semibold text-gray-900 mb-6">Manage Dealers</h2>
            <div className="overflow-x-auto">
              <table className="min-w-full bg-white shadow-md rounded-lg">
                <thead className="bg-gray-100">
                  <tr>
                    <th className="py-3 px-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                    <th className="py-3 px-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Name</th>
                    <th className="py-3 px-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Mobile Number</th>
                    <th className="py-3 px-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Address</th>
                    <th className="py-3 px-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                    <th className="py-3 px-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200">
                  {dealers.length > 0 ? (
                    dealers.map((dealer) => (
                      <tr key={dealer.userId}>
                        <td className="py-3 px-4 whitespace-nowrap text-sm text-gray-900">{dealer.userId}</td>
                        <td className="py-3 px-4 whitespace-nowrap text-sm text-gray-900">{dealer.name || 'N/A'}</td>
                        <td className="py-3 px-4 whitespace-nowrap text-sm text-gray-900">{dealer.mobileNumber || 'N/A'}</td>
                        <td className="py-3 px-4 whitespace-nowrap text-sm text-gray-900">{dealer.address || 'N/A'}</td>
                        <td className="py-3 px-4 whitespace-nowrap text-sm">
                          <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                            dealer.status ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                          }`}>
                            {dealer.status ? 'Active' : 'Inactive'}
                          </span>
                        </td>
                        <td className="py-3 px-4 whitespace-nowrap text-sm">
                          <button
                            onClick={() => handleUpdateUserStatus(dealer.userId, dealer.status, 'DEALER')}
                            className={`p-1 rounded-full ${
                                dealer.status ? 'bg-red-100 text-red-600 hover:bg-red-200' : 'bg-green-100 text-green-600 hover:bg-green-200'
                            }`}
                            disabled={loading}
                          >
                            {dealer.status ? <ToggleLeft size={20} /> : <ToggleRight size={20} />}
                          </button>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan="6" className="py-4 px-4 text-center text-gray-500">No dealers found.</td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* Manage Farmers Tab */}
        {activeTab === 'farmers' && (
          <div>
            <h2 className="text-xl font-semibold text-gray-900 mb-6">Manage Farmers</h2>
            <div className="overflow-x-auto">
              <table className="min-w-full bg-white shadow-md rounded-lg">
                <thead className="bg-gray-100">
                  <tr>
                    <th className="py-3 px-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                    <th className="py-3 px-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Name</th>
                    <th className="py-3 px-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Mobile Number</th>
                    <th className="py-3 px-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Address</th>
                    <th className="py-3 px-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                    <th className="py-3 px-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200">
                  {farmers.length > 0 ? (
                    farmers.map((farmer) => (
                      <tr key={farmer.userId}>
                        <td className="py-3 px-4 whitespace-nowrap text-sm text-gray-900">{farmer.userId}</td>
                        <td className="py-3 px-4 whitespace-nowrap text-sm text-gray-900">{farmer.name || 'N/A'}</td>
                        <td className="py-3 px-4 whitespace-nowrap text-sm text-gray-900">{farmer.mobileNumber || 'N/A'}</td>
                        <td className="py-3 px-4 whitespace-nowrap text-sm text-gray-900">{farmer.address || 'N/A'}</td>
                        <td className="py-3 px-4 whitespace-nowrap text-sm">
                          <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                            farmer.status ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                          }`}>
                            {farmer.status ? 'Active' : 'Inactive'}
                          </span>
                        </td>
                        <td className="py-3 px-4 whitespace-nowrap text-sm">
                          <button
                            onClick={() => handleUpdateUserStatus(farmer.userId, farmer.status, 'FARMER')}
                            className={`p-1 rounded-full ${
                                farmer.status ? 'bg-red-100 text-red-600 hover:bg-red-200' : 'bg-green-100 text-green-600 hover:bg-green-200'
                            }`}
                            disabled={loading}
                          >
                            {farmer.status ? <ToggleLeft size={20} /> : <ToggleRight size={20} />}
                          </button>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan="6" className="py-4 px-4 text-center text-gray-500">No farmers found.</td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* All Orders Tab */}
        {activeTab === 'orders' && (
          <div>
            <h2 className="text-xl font-semibold text-gray-900 mb-6">All Orders</h2>
            <div className="space-y-4">
              {orders.length > 0 ? (
                orders.map((order) => (
                  <div key={order.orderID} className="bg-white rounded-lg shadow-md p-6">
                    <div className="flex justify-between items-start mb-4">
                      <div>
                        <h3 className="text-lg font-semibold text-gray-900">Order ID: {order.orderID.slice(0, 8)}...</h3>
                        <span className={`inline-block px-2 py-1 rounded-full text-xs font-medium ${
                          order.orderStatus === 'Out for delivery'
                            ? 'bg-yellow-100 text-yellow-800'
                            : 'bg-green-100 text-green-800'
                        }`}>
                          {order.orderStatus}
                        </span>
                      </div>
                      <div className="text-right">
                        <div className="text-2xl font-bold text-green-600">₹{order.totalPrice}</div>
                      </div>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div>
                        <h4 className="font-medium text-gray-900 mb-2">Crop Details</h4>
                        <p><strong>Crop Name:</strong> {order.cropName}</p>
                        <p><strong>Quantity:</strong> {order.quantity} kg</p>
                        <p><strong>Price/kg:</strong> ₹{order.cropPrice}</p>
                        <p><strong>Total Amount:</strong>₹{order.totalPrice.toFixed(2)}</p>
                      </div>

                      <div>
                        <h4 className="font-medium text-gray-900 mb-2">Farmer Details</h4>
                        <p><strong>Name:</strong> {order.farmerName}</p>
                        <p><strong>Mobile:</strong> {order.farmerMobile}</p>
                        <p><strong>Address:</strong> {order.farmerAddress}</p>
                      </div>

                      <div>
                        <h4 className="font-medium text-gray-900 mb-2">Dealer Details</h4>
                        <p><strong>Name:</strong> {order.dealerName}</p>
                        <p><strong>Mobile:</strong> {order.dealerMobile}</p>
                        <p><strong>Address:</strong> {order.dealerAddress}</p>
                      </div>
                    </div>
                  </div>
                ))
              ) : (
                <p className="text-center text-gray-600">No orders found.</p>
              )}
            </div>
          </div>
        )}
      </div>
</main>
      {/* Profile Modal */}
      {showProfileModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-lg font-semibold">Admin Profile</h3>
              <button onClick={() => setShowProfileModal(false)}>
                <X size={20} />
              </button>
            </div>

            {adminProfile ? (
              <div className="space-y-3">
                <div>
                  <label className="block text-sm font-medium text-gray-700">Username</label>
                  <p className="text-gray-900">{adminProfile.username || 'N/A'}</p>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">Role</label>
                  <p className="text-gray-900">{adminProfile.role}</p>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">Mobile Number</label>
                  <p className="text-gray-900">{adminProfile.mobileNumber || 'N/A'}</p>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">Address</label>
                  <p className="text-gray-900">{adminProfile.address || 'N/A'}</p>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">Active Status</label>
                  <p className={`text-gray-900 ${adminProfile.active ? 'text-green-600' : 'text-red-600'}`}>
                    {adminProfile.active ? 'Active' : 'Inactive'}
                  </p>
                </div>

                <button
                  onClick={() => {
                    setShowProfileModal(false);
                    setShowUpdateUpdateProfileModal(true); // Corrected typo here
                  }}
                  className="w-full bg-blue-600 text-white py-2 px-4 rounded hover:bg-blue-700"
                >
                  Update Profile
                </button>
              </div>
            ) : (
              <p className="text-center text-gray-600">Loading profile...</p>
            )}
          </div>
        </div>
      )}

      {/* Update Profile Modal */}
      {showUpdateProfileModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-lg font-semibold">Update Admin Profile</h3>
              <button onClick={() => setShowUpdateUpdateProfileModal(false)}>
                <X size={20} />
              </button>
            </div>

            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Address</label>
                <input
                  type="text"
                  value={profileUpdate.address}
                  onChange={(e) => setProfileUpdate({...profileUpdate, address: e.target.value})}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Mobile Number</label>
                <input
                  type="tel"
                  value={profileUpdate.mobileNumber}
                  onChange={(e) => setProfileUpdate({...profileUpdate, mobileNumber: e.target.value})}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                />
              </div>
              {/* Assuming active status can also be updated from self-profile, as per API */}
              {/* <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Active Status</label>
                <label className="inline-flex items-center cursor-pointer">
                    <input
                        type="checkbox"
                        className="sr-only peer"
                        checked={profileUpdate.active}
                        onChange={(e) => setProfileUpdate({...profileUpdate, active: e.target.checked})}
                    />
                    <div className="relative w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 rounded-full peer peer-checked:after:translate-x-full rtl:peer-checked:after:-translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:start-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-600"></div>
                    <span className="ms-3 text-sm font-medium text-gray-900">{profileUpdate.active ? 'Active' : 'Inactive'}</span>
                </label>
              </div> */}

              <div className="flex space-x-3">
                <button
                  onClick={() => setShowUpdateUpdateProfileModal(false)}
                  className="flex-1 bg-gray-300 text-gray-700 py-2 px-4 rounded hover:bg-gray-400"
                >
                  Cancel
                </button>
                <button
                  onClick={handleUpdateAdminProfile}
                  disabled={loading}
                  className="flex-1 bg-blue-600 text-white py-2 px-4 rounded hover:bg-blue-700 disabled:opacity-50"
                >
                  {loading ? 'Updating...' : 'Update'}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminDashboard;