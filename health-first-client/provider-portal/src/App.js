import React, { useState } from 'react';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import ProviderLogin from './components/ProviderLogin';
import ProviderRegistration from './components/ProviderRegistration';
import './App.css';

// Create a custom theme for the healthcare application
const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
      light: '#42a5f5',
      dark: '#1565c0',
    },
    secondary: {
      main: '#dc004e',
    },
    background: {
      default: '#f5f5f5',
    },
  },
  typography: {
    fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
    h4: {
      fontWeight: 600,
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
    MuiPaper: {
      styleOverrides: {
        root: {
          borderRadius: 12,
        },
      },
    },
  },
});

function App() {
  const [showLogin, setShowLogin] = useState(false);

  const handleSwitchToLogin = () => setShowLogin(true);
  const handleSwitchToRegister = () => setShowLogin(false);

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      {showLogin ? (
        <ProviderLogin onSwitchToRegister={handleSwitchToRegister} />
      ) : (
        <ProviderRegistration onSwitchToLogin={handleSwitchToLogin} />
      )}
    </ThemeProvider>
  );
}

export default App;
