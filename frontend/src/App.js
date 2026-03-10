import  { useEffect, useState } from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import AuthPage from './pages/AuthPage'; // Your authentication component
import FarmerDashboard from './pages/FarmerDashboard'; // Your farmer dashboard component
import DealerDashboard from './pages/DealerDashboard'; // Your dealer dashboard component
import AdminDashboard from './pages/AdminDashboard';   // Your admin dashboard component
//import './index.css';
const App = () => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [userRole, setUserRole] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('userRole');
    const userId = localStorage.getItem('userId');
    const username = localStorage.getItem('username');

    if (token && role && userId && username) {
      // Basic validation for existing token (can be enhanced with an API call)
      setIsAuthenticated(true);
      setUserRole(role);
    }
    setLoading(false);
  }, []);

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-gray-100">
        <div className="text-xl font-semibold text-gray-700">Loading application...</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-green-100 via-emerald-100 to-emerald-200">
      <Router>
        <Routes>
          {/* Public Route for Authentication */}
          <Route path="/" element={<AuthPage />} />

          {/* Protected Routes */}
          <Route
            path="/farmer-dashboard"
            element={
              isAuthenticated && userRole === 'FARMER' ? (
                <FarmerDashboard />
              ) : (
                <Navigate to="/" replace />
              )
            }
          />
          <Route
            path="/dealer-dashboard"
            element={
              isAuthenticated && userRole === 'DEALER' ? (
                <DealerDashboard />
              ) : (
                <Navigate to="/" replace />
              )
            }
          />
          <Route
            path="/admin-dashboard"
            element={
              isAuthenticated && userRole === 'ADMIN' ? (
                <AdminDashboard />
              ) : (
                <Navigate to="/" replace />
              )
            }
          />

          {/* Redirect authenticated users to their respective dashboards */}
          <Route
            path="/dashboard"
            element={
              isAuthenticated ? (
                userRole === 'FARMER' ? (
                  <Navigate to="/farmer-dashboard" replace />
                ) : userRole === 'DEALER' ? (
                  <Navigate to="/dealer-dashboard" replace />
                ) : userRole === 'ADMIN' ? (
                  <Navigate to="/admin-dashboard" replace />
                ) : (
                  <Navigate to="/" replace />
                )
              ) : (
                <Navigate to="/" replace />
              )
            }
          />

          {/* Fallback Route */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Router>
    </div>
  );
};

export default App;