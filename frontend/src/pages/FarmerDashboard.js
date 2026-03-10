import React, { useState, useEffect, useCallback, useMemo } from 'react';
import { User, Edit, Plus, MessageCircle, X, Send, Package, Menu, ShoppingCart, Trash2, Search, Filter } from 'lucide-react'; // Added Search and Filter icons

const FarmerDashboard = () => {
const [activeTab, setActiveTab] = useState('crops');
const [showProfile, setShowProfile] = useState(false);
const [showAddCrop, setShowAddCrop] = useState(false);
const [showUpdateCrop, setShowUpdateCrop] = useState(false);
const [showUpdateProfile, setShowUpdateProfile] = useState(false);
const [showChatbot, setShowChatbot] = useState(false);
const [loading, setLoading] = useState(false);
const [message, setMessage] = useState('');
const [messageType, setMessageType] = useState('');
const [isSidebarOpen, setIsSidebarOpen] = React.useState(true);

// New states for search, sort, filter, and pagination
const [searchTerm, setSearchTerm] = useState('');
const [sortOption, setSortOption] = useState(''); // e.g., 'nameAsc', 'nameDesc', 'qtyAsc', 'qtyDesc', 'priceAsc', 'priceDesc'
const [filterCropType, setFilterCropType] = useState(''); // e.g., 'Vegetable', 'Fruit', etc.
const [currentPage, setCurrentPage] = useState(1);
const [cropsPerPage] = useState(6); // Show 6 crops per page

// User data from localStorage
const [userId] = useState(localStorage.getItem('userId'));
const [token] = useState(localStorage.getItem('token'));
const [username] = useState(localStorage.getItem('username'));

// Profile data
const [profile, setProfile] = useState(null);
const [profileUpdate, setProfileUpdate] = useState({
address: '',
mobileNumber: ''
});

// Crops data
const [crops, setCrops] = useState([]);
const [newCrop, setNewCrop] = useState({
cropName: '',
cropType: '',
cropQty: '',
cropPrice: '',
cropDescription: '',
imageUrl: ''
});
const [currentCrop, setCurrentCrop] = useState(null);

// Orders data
const [orders, setOrders] = useState([]);

// Chatbot data
const [chatMessages, setChatMessages] = useState([]);
const [chatInput, setChatInput] = useState('');
const [chatbotLoading, setChatbotLoading] = useState(false);

// Timed Message Logic
useEffect(() => {
let timer;
if (message) {
timer = setTimeout(() => {
setMessage('');
setMessageType('');
}, 3000); // Message will disappear after 3 seconds
}
return () => clearTimeout(timer); // Cleanup the timer
}, [message]);

const handleLogout = () => {
localStorage.removeItem('token');
localStorage.removeItem('userId');
localStorage.removeItem('username');
window.location.href = '/login';
};

const fetchProfile = useCallback(async () => {
try {
const response = await fetch(`http://localhost:8888/api/farmer/profile/${userId}`, {
headers: { 'Authorization': `Bearer ${token}` }
});
if (response.ok) {
const data = await response.json();
setProfile(data);
setProfileUpdate({
address: data.user.address,
mobileNumber: data.user.mobileNumber
});
} else {
console.error('Failed to fetch profile:', response.statusText);
}
} catch (error) {
console.error('Error fetching profile:', error);
}
}, [userId, token]);

const fetchCrops = useCallback(async () => {
try {
const response = await fetch(`http://localhost:8888/api/farmer/crops/${userId}`, {
headers: { 'Authorization': `Bearer ${token}` }
});
if (response.ok) {
const data = await response.json();
setCrops(data);
} else {
console.error('Failed to fetch crops:', response.statusText);
}
} catch (error) {
console.error('Error fetching crops:', error);
}
}, [userId, token]);

const fetchOrders = useCallback(async () => {
try {
const response = await fetch(`http://localhost:8888/api/orders/getOrderByFarmer/${userId}`, {
headers: { 'Authorization': `Bearer ${token}` }
});
if (response.ok) {
const data = await response.json();
setOrders(data);
} else {
console.error('Failed to fetch orders:', response.statusText);
}
} catch (error) {
console.error('Error fetching orders:', error);
}
}, [userId, token]);

useEffect(() => {
if (activeTab === 'crops') {
fetchCrops();
}
if (activeTab === 'orders') {
fetchOrders();
}
}, [activeTab, fetchCrops, fetchOrders]);

const handleUpdateProfile = async () => {
setLoading(true);
try {
const response = await fetch(`http://localhost:8888/api/farmer/profile/${userId}`, {
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
setShowUpdateProfile(false);
fetchProfile();
} else {
const errorData = await response.json();
setMessage(`Failed to update profile: ${errorData.message || response.statusText}`);
setMessageType('error');
}
} catch (error) {
setMessage('Error updating profile');
setMessageType('error');
console.error('Error updating profile:', error);
} finally {
setLoading(false);
}
};

const handleAddCrop = async () => {
setLoading(true);
try {
const response = await fetch(`http://localhost:8888/api/farmer/crop/${userId}`, {
method: 'POST',
headers: {
'Authorization': `Bearer ${token}`,
'Content-Type': 'application/json'
},
body: JSON.stringify({
...newCrop,
cropQty: parseInt(newCrop.cropQty),
cropPrice: parseFloat(newCrop.cropPrice)
})
});
if (response.ok) {
setMessage('Crop added successfully!');
setMessageType('success');
setShowAddCrop(false);
setNewCrop({
cropName: '',
cropType: '',
cropQty: '',
cropPrice: '',
cropDescription: '',
imageUrl: ''
});
fetchCrops();
} else {
const errorData = await response.json();
setMessage(`Failed to add crop: ${errorData.message || response.statusText}`);
setMessageType('error');
}
} catch (error) {
setMessage('Error adding crop');
setMessageType('error');
console.error('Error adding crop:', error);
} finally {
setLoading(false);
}
};

const handleUpdateCrop = async () => {
setLoading(true);
try {
const response = await fetch(`http://localhost:8888/api/farmer/crop/${userId}/${currentCrop.id}`, {
method: 'PUT',
headers: {
'Authorization': `Bearer ${token}`,
'Content-Type': 'application/json'
},
body: JSON.stringify({
...currentCrop,
cropQty: parseInt(currentCrop.cropQty),
cropPrice: parseFloat(currentCrop.cropPrice)
})
});
if (response.ok) {
setMessage('Crop updated successfully!');
setMessageType('success');
setShowUpdateCrop(false);
setCurrentCrop(null);
fetchCrops();
} else {
const errorData = await response.json();
setMessage(`Failed to update crop: ${errorData.message || response.statusText}`);
setMessageType('error');
}
} catch (error) {
setMessage('Error updating crop');
setMessageType('error');
console.error('Error updating crop:', error);
} finally {
setLoading(false);
}
};

const handleDeleteCrop = async (cropId) => {
if (window.confirm('Are you sure you want to delete this crop?')) {
try {
const response = await fetch(`http://localhost:8888/api/farmer/crop/${userId}/${cropId}`, {
method: 'DELETE',
headers: { 'Authorization': `Bearer ${token}` }
});
if (response.ok) {
  const responseText = await response.text();

if (responseText === "Your crop is ordered by a dealer and cannot be deleted.") {
          setMessage('Your Crop is ordered by the dealer, Wait until crop reaches the dealer and admin approves');
          setMessageType('error'); // Set as error because it's a "cannot delete" scenario
        } else {
          // If response is OK and doesn't contain the specific message, it's a true success
          setMessage('Crop deleted successfully!');
          setMessageType('success');
          fetchCrops(); // Refresh the list of crops
        }
} else {
setMessage(`Your Crop is ordered by the dealer, you cannot delete it until after the crop is delivered.`);
setMessageType('error');
}
} catch (error) {
setMessage('Error deleting crop');
setMessageType('error');
console.error('Error deleting crop:', error);
}
}
};

const handleSendMessage = async (e) => {
e.preventDefault();
if (!chatInput.trim()) return;

const userMessage = { type: 'user', text: chatInput };
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
const botMessage = { type: 'bot', text: data.response };
setChatMessages(prev => [...prev, botMessage]);
} else {
const errorData = await response.json();
const errorMessage = { type: 'bot', text: `Error: ${errorData.message || 'Could not get a response.'}` };
setChatMessages(prev => [...prev, errorMessage]);
}
} catch (error) {
console.error("Error sending message to chatbot:", error);
const errorMessage = { type: 'bot', text: 'Sorry, I encountered an error. Please try again.' };
setChatMessages(prev => [...prev, errorMessage]);
} finally {
setChatbotLoading(false);
}
};

useEffect(() => {
const chatContainer = document.querySelector('.flex-1.p-3.overflow-y-auto');
if (chatContainer) {
chatContainer.scrollTop = chatContainer.scrollHeight;
}
}, [chatMessages]);

// --- Filtering, Sorting, and Pagination Logic ---
const filteredAndSortedCrops = useMemo(() => {
let actionableCrops = [...crops];

// 1. Filter by Search Term
if (searchTerm) {
actionableCrops = actionableCrops.filter(crop =>
crop.cropName.toLowerCase().includes(searchTerm.toLowerCase())
);
}

// 2. Filter by Crop Type
if (filterCropType) {
actionableCrops = actionableCrops.filter(crop =>
crop.cropType.toLowerCase() === filterCropType.toLowerCase()
);
}

// 3. Sort
actionableCrops.sort((a, b) => {
switch (sortOption) {
case 'nameAsc':
return a.cropName.localeCompare(b.cropName);
case 'nameDesc':
return b.cropName.localeCompare(a.cropName);
case 'qtyAsc':
return a.cropQty - b.cropQty;
case 'qtyDesc':
return b.cropQty - a.cropQty;
case 'priceAsc':
return a.cropPrice - b.cropPrice;
case 'priceDesc':
return b.cropPrice - a.cropPrice;
default:
return 0; // No sorting
}
});

return actionableCrops;
}, [crops, searchTerm, filterCropType, sortOption]);

// Pagination calculations
const indexOfLastCrop = currentPage * cropsPerPage;
const indexOfFirstCrop = indexOfLastCrop - cropsPerPage;
const currentCrops = filteredAndSortedCrops.slice(indexOfFirstCrop, indexOfLastCrop);

const totalPages = Math.ceil(filteredAndSortedCrops.length / cropsPerPage);

const paginate = (pageNumber) => setCurrentPage(pageNumber);

const handleClearFilters = () => {
setSearchTerm('');
setSortOption('');
setFilterCropType('');
setCurrentPage(1); // Reset to first page
};

// Reset page to 1 when filters or search change
useEffect(() => {
setCurrentPage(1);
}, [searchTerm, sortOption, filterCropType]);


return (
<div className="flex min-h-screen bg-gray-50">
{/* Sidebar */}
<aside className="w-64 bg-green-700 text-white shadow-md border-r border-green-800 p-4 flex flex-col">
<div className="flex items-center mb-6 mt-2">
<h1 className="text-4xl font-bold text-white">CropDeal</h1>
</div>
<hr/>
<br/>

<nav>
<button
onClick={() => { setActiveTab('crops'); setIsSidebarOpen(false); }}
className={`w-full flex items-center px-4 py-2 mt-4 rounded-md transition duration-200 ease-in-out ${
activeTab === 'crops'
? 'bg-green-800 text-white'
: 'text-green-100 hover:bg-green-600 hover:text-white'
}`}
>
<Package className="mr-3" size={30} />
My Crops
</button>
<br/>
<br/>
<button
onClick={() => { setActiveTab('orders'); setIsSidebarOpen(false); }}
className={`w-full flex items-center px-4 py-2 mt-2 rounded-md transition duration-200 ease-in-out ${
activeTab === 'orders'
? 'bg-green-800 text-white'
: 'text-green-100 hover:bg-green-600 hover:text-white'
}`}
>
<ShoppingCart className="mr-3" size={30} />
Orders
</button>
</nav>
</aside>

{/* Main Content Area */}
<main className="flex-1 flex flex-col">
{/* Header */}
<header className="bg-white shadow-sm border-b border-gray-200 sticky top-0 z-30">
<div className="max-w-full mx-auto px-4 sm:px-6 lg:px-8">
<div className="flex justify-between items-center h-16">
<div className="flex items-center">
<button
onClick={() => setIsSidebarOpen(!isSidebarOpen)}
className="md:hidden p-2 rounded-md text-gray-500 hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-inset focus:ring-green-500 mr-2"
>
<Menu size={24} />
</button>
<h1 className="text-2xl font-bold text-green-800">Farmer Dashboard</h1>
</div>
<div className="flex items-center space-x-4">
<h2> <span className="text-xl font-semibold text-gray-900">Welcome, {username}</span></h2>
<button
onClick={() => {
setShowProfile(true);
if (fetchProfile) fetchProfile();
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

{/* Message */}
{message && (
<div className={`mx-4 mt-4 p-3 rounded-lg ${
messageType === 'success' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
}`}>
{message}
<button onClick={() => setMessage('')} className="float-right">×</button>
</div>
)}

{/* Content Area within main */}
<div className="flex-1 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 w-full">
{/* Crops Tab */}
{activeTab === 'crops' && (
<div>
<div className="flex justify-between items-center mb-6 flex-wrap gap-4">
<h2 className="text-xl font-semibold text-gray-900">My Crops</h2>
<div className="flex items-center gap-3 w-full sm:w-auto">
<div className="relative flex-grow">
<input
type="text"
placeholder="Search crops by name..."
value={searchTerm}
onChange={(e) => setSearchTerm(e.target.value)}
className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-green-500 focus:border-green-500"
/>
<Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={18} />
</div>

<select
value={sortOption}
onChange={(e) => setSortOption(e.target.value)}
className="p-2 border border-gray-300 rounded-lg focus:ring-green-500 focus:border-green-500"
>
<option value="">Sort By</option>
<option value="nameAsc">Name (A-Z)</option>
<option value="nameDesc">Name (Z-A)</option>
<option value="qtyAsc">Quantity (Low to High)</option>
<option value="qtyDesc">Quantity (High to Low)</option>
<option value="priceAsc">Price (Low to High)</option>
<option value="priceDesc">Price (High to Low)</option>
</select>

<select
value={filterCropType}
onChange={(e) => setFilterCropType(e.target.value)}
className="p-2 border border-gray-300 rounded-lg focus:ring-green-500 focus:border-green-500"
>
<option value="">All Types</option>
<option value="Vegetable">Vegetable</option>
<option value="Fruit">Fruit</option>
<option value="Grain">Grain</option>
<option value="Spice">Spice</option>
<option value="Dry Fruit">Dry Fruit</option>
</select>

<button
onClick={handleClearFilters}
className="bg-gray-200 text-gray-700 px-4 py-2 rounded-lg hover:bg-gray-300 flex items-center whitespace-nowrap"
>
<Filter className="mr-2" size={16} />
Clear Filters
</button>
<button
onClick={() => setShowAddCrop(true)}
className="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 flex items-center whitespace-nowrap"
>
<Plus className="mr-2" size={16} />
Add Crop
</button>
</div>
</div>

<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
{currentCrops.length > 0 ? (
currentCrops.map((crop) => (
<div key={crop.id} className="bg-white rounded-lg shadow-md overflow-hidden">
<img
src={crop.imageUrl}
alt={crop.cropName}
className="w-full h-48 object-cover"
/>
<div className="p-4">
<h3 className="text-lg font-semibold text-gray-900">{crop.cropName}</h3>
<p className="text-sm text-gray-600 mb-2">{crop.cropType}</p>
<p className="text-gray-700 mb-2">{crop.cropDescription}</p>
<div className="flex justify-between items-center mb-3">
<span className="text-green-600 font-semibold">₹{crop.cropPrice}/kg</span>
<span className="text-gray-600">{crop.cropQty} kg available</span>
</div>
<p className="text-sm text-gray-500">Farmer: {crop.farmer?.user?.username || 'N/A'}</p>
<div className="flex space-x-2 mt-4">
<button
onClick={() => {
setCurrentCrop(crop);
setShowUpdateCrop(true);
}}
className="flex-1 bg-blue-600 text-white py-2 px-3 rounded hover:bg-blue-700 flex items-center justify-center"
>
<Edit size={16} className="mr-1" />
Update
</button>
<button
onClick={() => handleDeleteCrop(crop.id)}
className="flex-1 bg-red-600 text-white py-2 px-3 rounded hover:bg-red-700 flex items-center justify-center"
>
<Trash2 size={16} className="mr-1" />
Delete
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
{filteredAndSortedCrops.length > cropsPerPage && (
<div className="flex justify-center items-center mt-8 space-x-2">
<button
onClick={() => paginate(currentPage - 1)}
disabled={currentPage === 1}
className="px-4 py-2 border rounded-lg bg-white text-gray-700 hover:bg-gray-100 disabled:opacity-50"
>
Prev
</button>
{Array.from({ length: totalPages }, (_, i) => (
<button
key={i + 1}
onClick={() => paginate(i + 1)}
className={`px-4 py-2 border rounded-lg ${
currentPage === i + 1 ? 'bg-green-600 text-white' : 'bg-white text-gray-700 hover:bg-gray-100'
}`}
>
{i + 1}
</button>
))}
<button
onClick={() => paginate(currentPage + 1)}
disabled={currentPage === totalPages}
className="px-4 py-2 border rounded-lg bg-white text-gray-700 hover:bg-gray-100 disabled:opacity-50"
>
Next
</button>
</div>
)}
</div>
)}

{/* Orders Tab */}
{activeTab === 'orders' && (
<div>
<h2 className="text-xl font-semibold text-gray-900 mb-6">My Orders</h2>
<div className="space-y-4">
{orders.length > 0 ? (
orders.map((order) => (
<div key={order.orderID} className="bg-white rounded-lg shadow-md p-6">
<div className="flex justify-between items-start mb-4">
<div>
<h3 className="text-lg font-semibold text-gray-900">Order #{order.orderID?.slice(0, 8)}</h3>
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
<p><strong>Crop:</strong> {order.cropName}</p>
<p><strong>Quantity:</strong> {order.quantity} kg</p>
<p><strong>Price:</strong> ₹{order.cropPrice}/kg</p>
<p><strong>Total amount:</strong>₹{order.totalPrice.toFixed(2)}</p>
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
<p className="text-center text-gray-600">No orders received yet.</p>
)}
</div>
</div>
)}
</div>
</main>

{/* Profile Modal */}
{showProfile && (
<div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
<div className="bg-white rounded-lg p-6 w-full max-w-md">
<div className="flex justify-between items-center mb-4">
<h3 className="text-lg font-semibold">Profile</h3>
<button onClick={() => setShowProfile(false)}>
<X size={20} />
</button>
</div>

{profile ? (
<div className="space-y-3">
<div>
<label className="block text-sm font-medium text-gray-700">Username</label>
<p className="text-gray-900">{profile.user.username}</p>
</div>
<div>
<label className="block text-sm font-medium text-gray-700">Role</label>
<p className="text-gray-900">{profile.user.role}</p>
</div>
<div>
<label className="block text-sm font-medium text-gray-700">Mobile Number</label>
<p className="text-gray-900">{profile.user.mobileNumber}</p>
</div>
<div>
<label className="block text-sm font-medium text-gray-700">Address</label>
<p className="text-gray-900">{profile.user.address}</p>
</div>

<button
onClick={() => {
setShowProfile(false);
setShowUpdateProfile(true);
}}
className="w-full bg-green-600 text-white py-2 px-4 rounded hover:bg-green-700"
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
{showUpdateProfile && (
<div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
<div className="bg-white rounded-lg p-6 w-full max-w-md">
<div className="flex justify-between items-center mb-4">
<h3 className="text-lg font-semibold">Update Profile</h3>
<button onClick={() => setShowUpdateProfile(false)}>
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
className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
/>
</div>
<div>
<label className="block text-sm font-medium text-gray-700 mb-1">Mobile Number</label>
<input
type="tel"
value={profileUpdate.mobileNumber}
onChange={(e) => setProfileUpdate({...profileUpdate, mobileNumber: e.target.value})}
className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
/>
</div>

<div className="flex space-x-3">
<button
onClick={() => setShowUpdateProfile(false)}
className="flex-1 bg-gray-300 text-gray-700 py-2 px-4 rounded hover:bg-gray-400"
>
Cancel
</button>
<button
onClick={handleUpdateProfile}
disabled={loading}
className="flex-1 bg-green-600 text-white py-2 px-4 rounded hover:bg-green-700 disabled:opacity-50"
>
{loading ? 'Updating...' : 'Update'}
</button>
</div>
</div>
</div>
</div>
)}

{/* Add Crop Modal */}
{showAddCrop && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md max-h-[90vh] overflow-y-auto">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-lg font-semibold">Add New Crop</h3>
              <button onClick={() => setShowAddCrop(false)}>
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleAddCrop} className="space-y-4">
             <div>
  <label className="block text-sm font-medium text-gray-700 mb-1">Crop Name</label>
  <input
    type="text"
    value={newCrop.cropName}
    pattern="^[A-Za-z\s]+$"
    required
    onChange={(e) => setNewCrop({ ...newCrop, cropName: e.target.value })}
    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
    onInvalid={(e) => {
      if (!e.target.value) {
        e.target.setCustomValidity("Crop Name should be filled.");
      } else if (!/^[A-Za-z\s]+$/.test(e.target.value)) {
        e.target.setCustomValidity("Crop Name should contain only alphabets.");
      }
    }}
    onInput={(e) => e.target.setCustomValidity("")}
  />
</div>
            <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Crop Type</label>
                <select
                  value={newCrop.cropType}
                  onChange={(e) => setNewCrop({ ...newCrop, cropType: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
                  required
                  onInvalid={(e) => e.target.setCustomValidity("Please select a Crop Type.")}
                  onInput={(e) => e.target.setCustomValidity("")}
                >
                  <option value="">Select Crop Type</option>
                  <option value="Vegetable">Vegetable</option>
                  <option value="Fruit">Fruit</option>
                  <option value="Grain">Grain</option>
                  <option value="Spice">Spice</option>
                  <option value="Dry Fruit">Dry Fruit</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Quantity (kg)</label>
                <input
                  type="number"
                  value={newCrop.cropQty}
                  onChange={(e) => setNewCrop({ ...newCrop, cropQty: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
                  required
                  pattern="^(1000|[1-9][0-9]{0,2})$"
                  minLength={1}
                  maxLength={4}
                  min="1" 
                  max="1000"
                  // Ensures a positive number (minimum value)
                  onInvalid={(e) => e.target.setCustomValidity("Quantity must be a filled and within allowed range.")}
                  onInput={(e) => e.target.setCustomValidity("")}
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Price (₹/kg)</label>
                <input
                  type="number"
                  step="1"
                  value={newCrop.cropPrice}
                  onChange={(e) => setNewCrop({ ...newCrop, cropPrice: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
                  required
                  min="1" // Ensures a positive number (minimum value)
                  onInvalid={(e) => e.target.setCustomValidity("Price must be filled.")}
                  onInput={(e) => e.target.setCustomValidity("")}
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
                <textarea
                  value={newCrop.cropDescription}
                  onChange={(e) => setNewCrop({ ...newCrop, cropDescription: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
                  rows="3"
                  required
                  onInvalid={(e) => e.target.setCustomValidity("Description is required.")}
                  onInput={(e) => e.target.setCustomValidity("")}
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Image URL</label>
                <input
                  type="url"
                  value={newCrop.imageUrl}
                  onChange={(e) => setNewCrop({ ...newCrop, imageUrl: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
                  required
                  onInvalid={(e) => e.target.setCustomValidity("Image URL is required and must be a valid URL (e.g., http://example.com/image.jpg).")}
                  onInput={(e) => e.target.setCustomValidity("")}
                />
              </div>

              <div className="flex space-x-3">
                <button
                  type="button"
                  onClick={() => setShowAddCrop(false)}
                  className="flex-1 bg-gray-300 text-gray-700 py-2 px-4 rounded hover:bg-gray-400"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={loading}
                  className="flex-1 bg-green-600 text-white py-2 px-4 rounded hover:bg-green-700 disabled:opacity-50"
                >
                  {loading ? 'Adding...' : 'Add Crop'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Update Crop Modal */}
      {showUpdateCrop && currentCrop && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md max-h-[90vh] overflow-y-auto">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-lg font-semibold">Update Crop</h3>
              <button onClick={() => setShowUpdateCrop(false)}>
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleUpdateCrop} className="space-y-4">
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Crop Name</label>
                    <input
                        type="text"
                        value={currentCrop.cropName}
                        pattern="^[A-Za-z\s]+$" // Added pattern
                        onChange={(e) => setCurrentCrop({ ...currentCrop, cropName: e.target.value })}
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
                        required
                        onInvalid={(e) => {
                            if (!e.target.value) {
                                e.target.setCustomValidity("Crop Name should be filled.");
                            } else if (!/^[A-Za-z\s]+$/.test(e.target.value)) {
                                e.target.setCustomValidity("Crop Name should contain only alphabets and spaces.");
                            }
                        }}
                        onInput={(e) => e.target.setCustomValidity("")}
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Crop Type</label>
                    <select
                        value={currentCrop.cropType}
                        onChange={(e) => setCurrentCrop({ ...currentCrop, cropType: e.target.value })}
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
                        required
                        onInvalid={(e) => e.target.setCustomValidity("Please select a Crop Type.")}
                        onInput={(e) => e.target.setCustomValidity("")}
                    >
                        <option value="">Select Crop Type</option>
                        <option value="Vegetable">Vegetable</option>
                        <option value="Fruit">Fruit</option>
                        <option value="Grain">Grain</option>
                        <option value="Spice">Spice</option>
                        <option value="Dry Fruit">Dry Fruit</option>
                    </select>
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Quantity (kg)</label>
                    <input
                        type="number"
                        value={currentCrop.cropQty}
                        onChange={(e) => setCurrentCrop({ ...currentCrop, cropQty: e.target.value })}
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
                        required
                        min="1"
                        onInvalid={(e) => {
                            if (!e.target.value) {
                                e.target.setCustomValidity("Quantity must be filled.");
                            } else if (e.target.value < 1) {
                                e.target.setCustomValidity("Quantity must be a positive number (minimum 1 kg).");
                            }
                        }}
                        onInput={(e) => e.target.setCustomValidity("")}
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Price (₹/kg)</label>
                    <input
                        type="number"
                        step="1"
                        value={currentCrop.cropPrice}
                        onChange={(e) => setCurrentCrop({ ...currentCrop, cropPrice: e.target.value })}
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
                        required
                        min="1"
                        onInvalid={(e) => {
                            if (!e.target.value) {
                                e.target.setCustomValidity("Price must be filled.");
                            } else if (e.target.value < 1) {
                                e.target.setCustomValidity("Price must be a positive number (minimum ₹1/kg).");
                            }
                        }}
                        onInput={(e) => e.target.setCustomValidity("")}
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
                    <textarea
                        value={currentCrop.cropDescription}
                        onChange={(e) => setCurrentCrop({ ...currentCrop, cropDescription: e.target.value })}
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
                        rows="3"
                        required
                        onInvalid={(e) => e.target.setCustomValidity("Description is required.")}
                        onInput={(e) => e.target.setCustomValidity("")}
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Image URL</label>
                    <input
                        type="url"
                        value={currentCrop.imageUrl}
                        onChange={(e) => setCurrentCrop({ ...currentCrop, imageUrl: e.target.value })}
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
                        required
                        onInvalid={(e) => e.target.setCustomValidity("Image URL is required and must be a valid URL (e.g., http://example.com/image.jpg).")}
                        onInput={(e) => e.target.setCustomValidity("")}
                    />
                </div>

                <div className="flex space-x-3">
                    <button
                        type="button"
                        onClick={() => setShowUpdateCrop(false)}
                        className="flex-1 bg-gray-300 text-gray-700 py-2 px-4 rounded hover:bg-gray-400"
                    >
                        Cancel
                    </button>
                    <button
                        type="submit"
                        disabled={loading}
                        className="flex-1 bg-green-600 text-white py-2 px-4 rounded hover:bg-green-700 disabled:opacity-50"
                    >
                        {loading ? 'Updating...' : 'Update'}
                    </button>
                </div>
            </form>
          </div>
        </div>
      )}
{/* Chatbot */}
<div className="fixed bottom-4 right-4 z-50">
{!showChatbot ? (
<button
onClick={() => setShowChatbot(true)}
className="bg-green-600 text-white p-3 rounded-full shadow-lg hover:bg-green-700"
>
<MessageCircle size={24} />
</button>
) : (
<div className="bg-white rounded-lg shadow-xl w-80 h-96 flex flex-col">
<div className="bg-green-600 text-white p-3 rounded-t-lg flex justify-between items-center">
<h4 className="font-medium">Crop Assistant</h4>
<button onClick={() => setShowChatbot(false)}>
<X size={20} />
</button>
</div>

<div className="flex-1 p-3 overflow-y-auto space-y-2">
{chatMessages.map((msg, index) => (
<div
key={index}
className={`p-2 rounded-lg max-w-[80%] ${
msg.type === 'user'
? 'bg-green-100 text-green-800 ml-auto'
: 'bg-gray-100 text-gray-800 mr-auto'
}`}
>
{msg.text}
</div>
))}
{chatbotLoading && (
<div className="p-2 rounded-lg bg-gray-100 text-gray-800 mr-auto">
Typing...
</div>
)}
</div>

<form onSubmit={handleSendMessage} className="p-3 border-t border-gray-200 flex">
<input
type="text"
value={chatInput}
onChange={(e) => setChatInput(e.target.value)}
placeholder="Type your message..."
className="flex-1 p-2 border rounded-l-lg focus:outline-none focus:ring-2 focus:ring-green-500"
disabled={chatbotLoading}
/>
<button
type="submit"
className="bg-green-600 text-white p-2 rounded-r-lg hover:bg-green-700"
disabled={chatbotLoading}
>
<Send size={20} />
</button>
</form>
</div>
)}
</div>
</div>
);
};

export default FarmerDashboard;