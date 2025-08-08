import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Container,
  Grid,
  Card,
  CardContent,
  Typography,
  Button,
  AppBar,
  Toolbar,
  IconButton,
  Drawer,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Divider,
  Chip,
  Avatar,
  Badge,
  Alert,
  LinearProgress,
} from '@mui/material';
import {
  Menu,
  Dashboard,
  CalendarToday,
  People,
  MonetizationOn,
  Notifications,
  Person,
  MedicalServices,
  Schedule,
  TrendingUp,
  ExitToApp,
  ChevronRight,
  LocalHospital,
  Event,
  Assignment,
  Warning,
} from '@mui/icons-material';

const UnifiedDashboard = ({ userRole, onLogout }) => {
  const navigate = useNavigate();
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [notifications] = useState([
    { id: 1, message: 'New appointment request from John Doe', type: 'info', time: '2 min ago' },
    { id: 2, message: 'Appointment cancelled for tomorrow', type: 'warning', time: '1 hour ago' },
    { id: 3, message: 'Payment received for consultation', type: 'success', time: '3 hours ago' },
  ]);

  // Mock data for providers
  const providerMetrics = {
    appointmentsToday: 8,
    appointmentsThisWeek: 45,
    revenueToday: 1200,
    revenueThisWeek: 8500,
    patientsWaitlist: 12,
    averageRating: 4.8,
  };

  // Mock data for patients
  const patientMetrics = {
    nextAppointment: 'Dr. Smith - Tomorrow 2:00 PM',
    prescriptionRefills: 2,
    healthReminders: 3,
    upcomingAppointments: 2,
    completedAppointments: 15,
  };

  const handleLogout = () => {
    onLogout();
    navigate('/login');
  };

  const toggleDrawer = () => {
    setDrawerOpen(!drawerOpen);
  };

  const renderProviderMetrics = () => (
    <Grid container spacing={3}>
      <Grid item xs={12} sm={6} md={3}>
        <Card>
          <CardContent>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
              <CalendarToday color="primary" sx={{ mr: 1 }} />
              <Typography variant="h6">Today's Appointments</Typography>
            </Box>
            <Typography variant="h4" color="primary">
              {providerMetrics.appointmentsToday}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {providerMetrics.appointmentsThisWeek} this week
            </Typography>
          </CardContent>
        </Card>
      </Grid>

      <Grid item xs={12} sm={6} md={3}>
        <Card>
          <CardContent>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
              <MonetizationOn color="success" sx={{ mr: 1 }} />
              <Typography variant="h6">Today's Revenue</Typography>
            </Box>
            <Typography variant="h4" color="success.main">
              ${providerMetrics.revenueToday}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              ${providerMetrics.revenueThisWeek} this week
            </Typography>
          </CardContent>
        </Card>
      </Grid>

      <Grid item xs={12} sm={6} md={3}>
        <Card>
          <CardContent>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
              <People color="warning" sx={{ mr: 1 }} />
              <Typography variant="h6">Waitlist</Typography>
            </Box>
            <Typography variant="h4" color="warning.main">
              {providerMetrics.patientsWaitlist}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Patients waiting
            </Typography>
          </CardContent>
        </Card>
      </Grid>

      <Grid item xs={12} sm={6} md={3}>
        <Card>
          <CardContent>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
              <TrendingUp color="info" sx={{ mr: 1 }} />
              <Typography variant="h6">Rating</Typography>
            </Box>
            <Typography variant="h4" color="info.main">
              {providerMetrics.averageRating}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Average patient rating
            </Typography>
          </CardContent>
        </Card>
      </Grid>
    </Grid>
  );

  const renderPatientMetrics = () => (
    <Grid container spacing={3}>
      <Grid item xs={12} sm={6} md={4}>
        <Card>
          <CardContent>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
              <Schedule color="primary" sx={{ mr: 1 }} />
              <Typography variant="h6">Next Appointment</Typography>
            </Box>
            <Typography variant="body1" sx={{ mb: 1 }}>
              {patientMetrics.nextAppointment}
            </Typography>
            <Chip label="Confirmed" color="success" size="small" />
          </CardContent>
        </Card>
      </Grid>

      <Grid item xs={12} sm={6} md={4}>
        <Card>
          <CardContent>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
              <Assignment color="warning" sx={{ mr: 1 }} />
              <Typography variant="h6">Prescription Refills</Typography>
            </Box>
            <Typography variant="h4" color="warning.main">
              {patientMetrics.prescriptionRefills}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Pending refills
            </Typography>
          </CardContent>
        </Card>
      </Grid>

      <Grid item xs={12} sm={6} md={4}>
        <Card>
          <CardContent>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
              <LocalHospital color="info" sx={{ mr: 1 }} />
              <Typography variant="h6">Health Reminders</Typography>
            </Box>
            <Typography variant="h4" color="info.main">
              {patientMetrics.healthReminders}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Due this week
            </Typography>
          </CardContent>
        </Card>
      </Grid>
    </Grid>
  );

  const drawer = (
    <Box sx={{ width: 250 }}>
      <Box sx={{ p: 2, textAlign: 'center' }}>
        <Avatar sx={{ width: 56, height: 56, mx: 'auto', mb: 1 }}>
          <Person />
        </Avatar>
        <Typography variant="h6">
          {userRole === 'provider' ? 'Dr. Smith' : 'John Doe'}
        </Typography>
        <Chip 
          label={userRole === 'provider' ? 'Healthcare Provider' : 'Patient'} 
          color="primary" 
          size="small" 
        />
      </Box>
      <Divider />
      <List>
        <ListItem button onClick={() => navigate('/dashboard')}>
          <ListItemIcon><Dashboard /></ListItemIcon>
          <ListItemText primary="Dashboard" />
        </ListItem>
        <ListItem button onClick={() => navigate(`/${userRole}-dashboard`)}>
          <ListItemIcon>
            {userRole === 'provider' ? <MedicalServices /> : <Person />}
          </ListItemIcon>
          <ListItemText primary={`${userRole === 'provider' ? 'Provider' : 'Patient'} Dashboard`} />
        </ListItem>
        <ListItem button>
          <ListItemIcon><CalendarToday /></ListItemIcon>
          <ListItemText primary="Appointments" />
        </ListItem>
        <ListItem button>
          <ListItemIcon><People /></ListItemIcon>
          <ListItemText primary={userRole === 'provider' ? 'Patients' : 'Providers'} />
        </ListItem>
        {userRole === 'provider' && (
          <ListItem button>
            <ListItemIcon><MonetizationOn /></ListItemIcon>
            <ListItemText primary="Revenue" />
          </ListItem>
        )}
      </List>
      <Divider />
      <List>
        <ListItem button onClick={handleLogout}>
          <ListItemIcon><ExitToApp /></ListItemIcon>
          <ListItemText primary="Logout" />
        </ListItem>
      </List>
    </Box>
  );

  return (
    <Box sx={{ display: 'flex' }}>
      <AppBar position="fixed" sx={{ zIndex: (theme) => theme.zIndex.drawer + 1 }}>
        <Toolbar>
          <IconButton
            color="inherit"
            edge="start"
            onClick={toggleDrawer}
            sx={{ mr: 2 }}
          >
            <Menu />
          </IconButton>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            HealthFirst Dashboard
          </Typography>
          <IconButton color="inherit">
            <Badge badgeContent={notifications.length} color="error">
              <Notifications />
            </Badge>
          </IconButton>
        </Toolbar>
      </AppBar>

      <Drawer
        variant="temporary"
        open={drawerOpen}
        onClose={toggleDrawer}
        sx={{
          width: 250,
          flexShrink: 0,
          '& .MuiDrawer-paper': {
            width: 250,
            boxSizing: 'border-box',
          },
        }}
      >
        {drawer}
      </Drawer>

      <Box
        component="main"
        sx={{
          flexGrow: 1,
          p: 3,
          mt: 8,
        }}
      >
        <Container maxWidth="xl">
          <Box sx={{ mb: 4 }}>
            <Typography variant="h4" gutterBottom>
              Welcome back, {userRole === 'provider' ? 'Dr. Smith' : 'John'}!
            </Typography>
            <Typography variant="body1" color="text.secondary">
              Here's what's happening with your {userRole === 'provider' ? 'practice' : 'health'} today.
            </Typography>
          </Box>

          {/* Notifications */}
          <Box sx={{ mb: 4 }}>
            {notifications.map((notification) => (
              <Alert 
                key={notification.id} 
                severity={notification.type} 
                sx={{ mb: 1 }}
                action={
                  <Button color="inherit" size="small">
                    View
                  </Button>
                }
              >
                {notification.message}
              </Alert>
            ))}
          </Box>

          {/* Metrics */}
          <Box sx={{ mb: 4 }}>
            <Typography variant="h5" gutterBottom>
              {userRole === 'provider' ? 'Practice Overview' : 'Health Summary'}
            </Typography>
            {userRole === 'provider' ? renderProviderMetrics() : renderPatientMetrics()}
          </Box>

          {/* Quick Actions */}
          <Box sx={{ mb: 4 }}>
            <Typography variant="h5" gutterBottom>
              Quick Actions
            </Typography>
            <Grid container spacing={2}>
              <Grid item>
                <Button
                  variant="contained"
                  startIcon={<CalendarToday />}
                  onClick={() => navigate(`/${userRole}-dashboard`)}
                >
                  Manage {userRole === 'provider' ? 'Availability' : 'Appointments'}
                </Button>
              </Grid>
              <Grid item>
                <Button
                  variant="outlined"
                  startIcon={<People />}
                >
                  {userRole === 'provider' ? 'View Patients' : 'Find Providers'}
                </Button>
              </Grid>
              {userRole === 'provider' && (
                <Grid item>
                  <Button
                    variant="outlined"
                    startIcon={<MonetizationOn />}
                  >
                    View Revenue
                  </Button>
                </Grid>
              )}
            </Grid>
          </Box>

          {/* Recent Activity */}
          <Box>
            <Typography variant="h5" gutterBottom>
              Recent Activity
            </Typography>
            <Card>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                  <Event color="primary" sx={{ mr: 1 }} />
                  <Typography variant="h6">Latest Appointments</Typography>
                </Box>
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 1 }}>
                  <Typography variant="body1">
                    {userRole === 'provider' ? 'Patient: John Doe' : 'Dr. Smith'}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Today 2:00 PM
                  </Typography>
                </Box>
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 1 }}>
                  <Typography variant="body1">
                    {userRole === 'provider' ? 'Patient: Jane Smith' : 'Dr. Johnson'}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Tomorrow 10:00 AM
                  </Typography>
                </Box>
                <Button
                  variant="text"
                  endIcon={<ChevronRight />}
                  sx={{ mt: 1 }}
                >
                  View All
                </Button>
              </CardContent>
            </Card>
          </Box>
        </Container>
      </Box>
    </Box>
  );
};

export default UnifiedDashboard; 