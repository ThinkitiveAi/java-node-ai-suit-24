import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import { CssBaseline, Box } from '@mui/material';
import PatientLogin from './components/PatientLogin';
import PatientRegistration from './components/PatientRegistration';
import ProviderDashboard from './components/dashboard/ProviderDashboard';
import PatientDashboard from './components/dashboard/PatientDashboard';
import UnifiedDashboard from './components/dashboard/UnifiedDashboard';
import './App.css';

const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
    background: {
      default: '#f5f5f5',
    },
    success: {
      main: '#2e7d32',
    },
    warning: {
      main: '#ed6c02',
    },
    error: {
      main: '#d32f2f',
    },
  },
  typography: {
    fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
    h4: {
      fontWeight: 600,
    },
    h5: {
      fontWeight: 500,
    },
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none',
          borderRadius: 8,
        },
      },
    },
    MuiTextField: {
      styleOverrides: {
        root: {
          '& .MuiOutlinedInput-root': {
            borderRadius: 8,
          },
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 12,
          boxShadow: '0 4px 20px rgba(0,0,0,0.1)',
        },
      },
    },
  },
});

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [userRole, setUserRole] = useState('patient'); // 'patient' or 'provider'

  const handleLogin = (credentials) => {
    // TODO: Implement actual API call
    console.log('Login attempt:', credentials);
    // Simulate successful login
    setIsAuthenticated(true);
    // Mock role detection - in real app this would come from API
    setUserRole(credentials.email.includes('provider') ? 'provider' : 'patient');
  };

  const handleRegistration = (userData) => {
    // TODO: Implement actual API call
    console.log('Registration attempt:', userData);
    // Simulate successful registration
    setIsAuthenticated(true);
    setUserRole('patient'); // New registrations are patients by default
  };

  const handleLogout = () => {
    setIsAuthenticated(false);
    setUserRole('patient');
  };

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Router>
        <Box sx={{ minHeight: '100vh', bgcolor: 'background.default' }}>
          <Routes>
            <Route 
              path="/" 
              element={
                isAuthenticated ? 
                <Navigate to="/dashboard" replace /> : 
                <Navigate to="/login" replace />
              } 
            />
            <Route 
              path="/login" 
              element={
                <PatientLogin 
                  onLogin={handleLogin}
                  isAuthenticated={isAuthenticated}
                />
              } 
            />
            <Route 
              path="/register" 
              element={
                <PatientRegistration 
                  onRegistration={handleRegistration}
                  isAuthenticated={isAuthenticated}
                />
              } 
            />
            <Route 
              path="/dashboard" 
              element={
                isAuthenticated ? 
                <UnifiedDashboard 
                  userRole={userRole}
                  onLogout={handleLogout}
                /> : 
                <Navigate to="/login" replace />
              } 
            />
            <Route 
              path="/provider-dashboard" 
              element={
                isAuthenticated && userRole === 'provider' ? 
                <ProviderDashboard 
                  onLogout={handleLogout}
                /> : 
                <Navigate to="/login" replace />
              } 
            />
            <Route 
              path="/patient-dashboard" 
              element={
                isAuthenticated && userRole === 'patient' ? 
                <PatientDashboard 
                  onLogout={handleLogout}
                /> : 
                <Navigate to="/login" replace />
              } 
            />
          </Routes>
        </Box>
      </Router>
    </ThemeProvider>
  );
}

export default App;
