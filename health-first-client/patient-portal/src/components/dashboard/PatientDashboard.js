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
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Rating,
  Alert,
  LinearProgress,
  Tabs,
  Tab,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
} from '@mui/material';
import {
  Menu,
  Dashboard,
  CalendarToday,
  People,
  Notifications,
  Person,
  MedicalServices,
  Schedule,
  Add,
  Search,
  LocationOn,
  Star,
  Phone,
  VideoCall,
  ExitToApp,
  ChevronRight,
  LocalHospital,
  Event,
  Assignment,
} from '@mui/icons-material';

const PatientDashboard = ({ onLogout }) => {
  const navigate = useNavigate();
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [tabValue, setTabValue] = useState(0);
  const [bookingDialogOpen, setBookingDialogOpen] = useState(false);
  const [selectedProvider, setSelectedProvider] = useState(null);
  const [searchFilters, setSearchFilters] = useState({
    specialty: '',
    date: '',
    location: '',
    priceRange: '',
  });

  const [notifications] = useState([
    { id: 1, message: 'Appointment confirmed with Dr. Smith', type: 'success', time: '1 hour ago' },
    { id: 2, message: 'Prescription refill reminder', type: 'info', time: '3 hours ago' },
    { id: 3, message: 'New message from Dr. Johnson', type: 'info', time: '1 day ago' },
  ]);

  // Mock data
  const [providers] = useState([
    {
      id: 1,
      name: 'Dr. Sarah Smith',
      specialty: 'Cardiology',
      rating: 4.8,
      reviews: 127,
      location: 'Downtown Medical Center',
      price: 150,
      availableSlots: ['09:00 AM', '02:00 PM', '04:00 PM'],
      image: 'https://via.placeholder.com/60',
    },
    {
      id: 2,
      name: 'Dr. Michael Johnson',
      specialty: 'Dermatology',
      rating: 4.6,
      reviews: 89,
      location: 'Westside Clinic',
      price: 120,
      availableSlots: ['10:00 AM', '03:00 PM'],
      image: 'https://via.placeholder.com/60',
    },
    {
      id: 3,
      name: 'Dr. Emily Davis',
      specialty: 'Pediatrics',
      rating: 4.9,
      reviews: 203,
      location: 'Children\'s Hospital',
      price: 100,
      availableSlots: ['11:00 AM', '01:00 PM', '05:00 PM'],
      image: 'https://via.placeholder.com/60',
    },
  ]);

  const [appointments] = useState([
    {
      id: 1,
      provider: 'Dr. Sarah Smith',
      date: '2024-01-15',
      time: '02:00 PM',
      status: 'confirmed',
      type: 'Follow-up',
      isVirtual: true,
    },
    {
      id: 2,
      provider: 'Dr. Michael Johnson',
      date: '2024-01-18',
      time: '10:00 AM',
      status: 'pending',
      type: 'Consultation',
      isVirtual: false,
    },
    {
      id: 3,
      provider: 'Dr. Emily Davis',
      date: '2024-01-12',
      time: '11:00 AM',
      status: 'completed',
      type: 'Check-up',
      isVirtual: false,
    },
  ]);

  const handleLogout = () => {
    onLogout();
    navigate('/login');
  };

  const toggleDrawer = () => {
    setDrawerOpen(!drawerOpen);
  };

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  const handleBookAppointment = (provider) => {
    setSelectedProvider(provider);
    setBookingDialogOpen(true);
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'confirmed': return 'success';
      case 'pending': return 'warning';
      case 'completed': return 'info';
      case 'cancelled': return 'error';
      default: return 'default';
    }
  };

  const drawer = (
    <Box sx={{ width: 250 }}>
      <Box sx={{ p: 2, textAlign: 'center' }}>
        <Avatar sx={{ width: 56, height: 56, mx: 'auto', mb: 1 }}>
          <Person />
        </Avatar>
        <Typography variant="h6">John Doe</Typography>
        <Chip label="Patient" color="primary" size="small" />
      </Box>
      <Divider />
      <List>
        <ListItem button onClick={() => navigate('/dashboard')}>
          <ListItemIcon><Dashboard /></ListItemIcon>
          <ListItemText primary="Main Dashboard" />
        </ListItem>
        <ListItem button>
          <ListItemIcon><CalendarToday /></ListItemIcon>
          <ListItemText primary="My Appointments" />
        </ListItem>
        <ListItem button>
          <ListItemIcon><MedicalServices /></ListItemIcon>
          <ListItemText primary="Find Providers" />
        </ListItem>
        <ListItem button>
          <ListItemIcon><Assignment /></ListItemIcon>
          <ListItemText primary="Medical Records" />
        </ListItem>
        <ListItem button>
          <ListItemIcon><LocalHospital /></ListItemIcon>
          <ListItemText primary="Prescriptions" />
        </ListItem>
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
            Patient Dashboard
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
              Patient Dashboard
            </Typography>
            <Typography variant="body1" color="text.secondary">
              Manage your appointments and find healthcare providers.
            </Typography>
          </Box>

          {/* Quick Stats */}
          <Grid container spacing={3} sx={{ mb: 4 }}>
            <Grid item xs={12} sm={6} md={3}>
              <Card>
                <CardContent>
                  <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                    <CalendarToday color="primary" sx={{ mr: 1 }} />
                    <Typography variant="h6">Next Appointment</Typography>
                  </Box>
                  <Typography variant="h6" color="primary">
                    Tomorrow 2:00 PM
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Dr. Sarah Smith
                  </Typography>
                </CardContent>
              </Card>
            </Grid>

            <Grid item xs={12} sm={6} md={3}>
              <Card>
                <CardContent>
                  <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                    <Assignment color="warning" sx={{ mr: 1 }} />
                    <Typography variant="h6">Prescription Refills</Typography>
                  </Box>
                  <Typography variant="h4" color="warning.main">2</Typography>
                  <Typography variant="body2" color="text.secondary">
                    Due this week
                  </Typography>
                </CardContent>
              </Card>
            </Grid>

            <Grid item xs={12} sm={6} md={3}>
              <Card>
                <CardContent>
                  <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                    <LocalHospital color="info" sx={{ mr: 1 }} />
                    <Typography variant="h6">Health Reminders</Typography>
                  </Box>
                  <Typography variant="h4" color="info.main">3</Typography>
                  <Typography variant="body2" color="text.secondary">
                    Due this week
                  </Typography>
                </CardContent>
              </Card>
            </Grid>

            <Grid item xs={12} sm={6} md={3}>
              <Card>
                <CardContent>
                  <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                    <Schedule color="success" sx={{ mr: 1 }} />
                    <Typography variant="h6">Total Appointments</Typography>
                  </Box>
                  <Typography variant="h4" color="success.main">15</Typography>
                  <Typography variant="body2" color="text.secondary">
                    This year
                  </Typography>
                </CardContent>
              </Card>
            </Grid>
          </Grid>

          {/* Main Content Tabs */}
          <Card>
            <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
              <Tabs value={tabValue} onChange={handleTabChange}>
                <Tab label="Find Providers" />
                <Tab label="My Appointments" />
                <Tab label="Medical Records" />
              </Tabs>
            </Box>

            <Box sx={{ p: 3 }}>
              {tabValue === 0 && (
                <Box>
                  {/* Search Filters */}
                  <Card sx={{ mb: 3 }}>
                    <CardContent>
                      <Typography variant="h6" gutterBottom>
                        Search Filters
                      </Typography>
                      <Grid container spacing={2}>
                        <Grid item xs={12} sm={6} md={3}>
                          <FormControl fullWidth>
                            <InputLabel>Specialty</InputLabel>
                            <Select
                              value={searchFilters.specialty}
                              onChange={(e) => setSearchFilters({...searchFilters, specialty: e.target.value})}
                            >
                              <MenuItem value="">All Specialties</MenuItem>
                              <MenuItem value="cardiology">Cardiology</MenuItem>
                              <MenuItem value="dermatology">Dermatology</MenuItem>
                              <MenuItem value="pediatrics">Pediatrics</MenuItem>
                              <MenuItem value="orthopedics">Orthopedics</MenuItem>
                            </Select>
                          </FormControl>
                        </Grid>
                        <Grid item xs={12} sm={6} md={3}>
                          <TextField
                            fullWidth
                            label="Date"
                            type="date"
                            value={searchFilters.date}
                            onChange={(e) => setSearchFilters({...searchFilters, date: e.target.value})}
                            InputLabelProps={{ shrink: true }}
                          />
                        </Grid>
                        <Grid item xs={12} sm={6} md={3}>
                          <TextField
                            fullWidth
                            label="Location"
                            value={searchFilters.location}
                            onChange={(e) => setSearchFilters({...searchFilters, location: e.target.value})}
                            placeholder="City or ZIP"
                          />
                        </Grid>
                        <Grid item xs={12} sm={6} md={3}>
                          <FormControl fullWidth>
                            <InputLabel>Price Range</InputLabel>
                            <Select
                              value={searchFilters.priceRange}
                              onChange={(e) => setSearchFilters({...searchFilters, priceRange: e.target.value})}
                            >
                              <MenuItem value="">Any Price</MenuItem>
                              <MenuItem value="0-100">$0 - $100</MenuItem>
                              <MenuItem value="100-200">$100 - $200</MenuItem>
                              <MenuItem value="200+">$200+</MenuItem>
                            </Select>
                          </FormControl>
                        </Grid>
                      </Grid>
                      <Box sx={{ mt: 2 }}>
                        <Button
                          variant="contained"
                          startIcon={<Search />}
                          sx={{ mr: 1 }}
                        >
                          Search
                        </Button>
                        <Button
                          variant="outlined"
                          onClick={() => setSearchFilters({
                            specialty: '',
                            date: '',
                            location: '',
                            priceRange: '',
                          })}
                        >
                          Clear Filters
                        </Button>
                      </Box>
                    </CardContent>
                  </Card>

                  {/* Provider Results */}
                  <Typography variant="h6" gutterBottom>
                    Available Providers
                  </Typography>
                  <Grid container spacing={3}>
                    {providers.map((provider) => (
                      <Grid item xs={12} md={6} lg={4} key={provider.id}>
                        <Card>
                          <CardContent>
                            <Box sx={{ display: 'flex', alignItems: 'flex-start', mb: 2 }}>
                              <Avatar
                                src={provider.image}
                                sx={{ width: 60, height: 60, mr: 2 }}
                              >
                                <MedicalServices />
                              </Avatar>
                              <Box sx={{ flexGrow: 1 }}>
                                <Typography variant="h6" gutterBottom>
                                  {provider.name}
                                </Typography>
                                <Typography variant="body2" color="text.secondary" gutterBottom>
                                  {provider.specialty}
                                </Typography>
                                <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                                  <Rating value={provider.rating} precision={0.1} size="small" readOnly />
                                  <Typography variant="body2" sx={{ ml: 1 }}>
                                    ({provider.reviews} reviews)
                                  </Typography>
                                </Box>
                                <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                                  <LocationOn sx={{ fontSize: 16, mr: 0.5, color: 'text.secondary' }} />
                                  <Typography variant="body2" color="text.secondary">
                                    {provider.location}
                                  </Typography>
                                </Box>
                                <Typography variant="h6" color="primary">
                                  ${provider.price}
                                </Typography>
                              </Box>
                            </Box>

                            <Box sx={{ mb: 2 }}>
                              <Typography variant="body2" color="text.secondary" gutterBottom>
                                Available Slots:
                              </Typography>
                              <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                                {provider.availableSlots.map((slot, index) => (
                                  <Chip
                                    key={index}
                                    label={slot}
                                    size="small"
                                    variant="outlined"
                                    color="primary"
                                  />
                                ))}
                              </Box>
                            </Box>

                            <Button
                              variant="contained"
                              fullWidth
                              onClick={() => handleBookAppointment(provider)}
                            >
                              Book Appointment
                            </Button>
                          </CardContent>
                        </Card>
                      </Grid>
                    ))}
                  </Grid>
                </Box>
              )}

              {tabValue === 1 && (
                <Box>
                  <Typography variant="h6" gutterBottom>
                    My Appointments
                  </Typography>
                  <TableContainer component={Paper}>
                    <Table>
                      <TableHead>
                        <TableRow>
                          <TableCell>Provider</TableCell>
                          <TableCell>Date & Time</TableCell>
                          <TableCell>Type</TableCell>
                          <TableCell>Status</TableCell>
                          <TableCell>Actions</TableCell>
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {appointments.map((appointment) => (
                          <TableRow key={appointment.id}>
                            <TableCell>
                              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                <Avatar sx={{ width: 32, height: 32, mr: 1 }}>
                                  <MedicalServices />
                                </Avatar>
                                {appointment.provider}
                              </Box>
                            </TableCell>
                            <TableCell>
                              <Typography variant="body2">
                                {appointment.date}
                              </Typography>
                              <Typography variant="body2" color="text.secondary">
                                {appointment.time}
                              </Typography>
                            </TableCell>
                            <TableCell>{appointment.type}</TableCell>
                            <TableCell>
                              <Chip 
                                label={appointment.status} 
                                color={getStatusColor(appointment.status)} 
                                size="small" 
                              />
                            </TableCell>
                            <TableCell>
                              <Box sx={{ display: 'flex', gap: 1 }}>
                                {appointment.isVirtual && appointment.status === 'confirmed' && (
                                  <Button
                                    size="small"
                                    variant="contained"
                                    startIcon={<VideoCall />}
                                    color="success"
                                  >
                                    Join Call
                                  </Button>
                                )}
                                {appointment.status === 'pending' && (
                                  <>
                                    <Button size="small" variant="outlined">
                                      Reschedule
                                    </Button>
                                    <Button size="small" color="error" variant="outlined">
                                      Cancel
                                    </Button>
                                  </>
                                )}
                                {appointment.status === 'confirmed' && (
                                  <Button size="small" variant="outlined">
                                    View Details
                                  </Button>
                                )}
                              </Box>
                            </TableCell>
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                  </TableContainer>
                </Box>
              )}

              {tabValue === 2 && (
                <Box>
                  <Typography variant="h6" gutterBottom>
                    Medical Records
                  </Typography>
                  <Alert severity="info" sx={{ mb: 2 }}>
                    Your medical records are securely stored and accessible here.
                  </Alert>
                  <Grid container spacing={2}>
                    {[1, 2, 3, 4].map((record) => (
                      <Grid item xs={12} sm={6} md={4} key={record}>
                        <Card>
                          <CardContent>
                            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                              <Assignment color="primary" sx={{ mr: 1 }} />
                              <Typography variant="h6">Record #{record}</Typography>
                            </Box>
                            <Typography variant="body2" color="text.secondary" gutterBottom>
                              Date: 2024-01-{10 + record}
                            </Typography>
                            <Typography variant="body2" color="text.secondary" gutterBottom>
                              Type: Medical Report
                            </Typography>
                            <Button
                              variant="outlined"
                              size="small"
                              endIcon={<ChevronRight />}
                            >
                              View Details
                            </Button>
                          </CardContent>
                        </Card>
                      </Grid>
                    ))}
                  </Grid>
                </Box>
              )}
            </Box>
          </Card>
        </Container>
      </Box>

      {/* Booking Dialog */}
      <Dialog open={bookingDialogOpen} onClose={() => setBookingDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>
          Book Appointment with {selectedProvider?.name}
        </DialogTitle>
        <DialogContent>
          {selectedProvider && (
            <Box sx={{ mt: 2 }}>
              <Grid container spacing={2}>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Preferred Date"
                    type="date"
                    InputLabelProps={{ shrink: true }}
                  />
                </Grid>
                <Grid item xs={12}>
                  <FormControl fullWidth>
                    <InputLabel>Preferred Time</InputLabel>
                    <Select>
                      {selectedProvider.availableSlots.map((slot, index) => (
                        <MenuItem key={index} value={slot}>
                          {slot}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Reason for Visit"
                    multiline
                    rows={3}
                    placeholder="Please describe your symptoms or reason for the appointment..."
                  />
                </Grid>
                <Grid item xs={12}>
                  <FormControl fullWidth>
                    <InputLabel>Appointment Type</InputLabel>
                    <Select>
                      <MenuItem value="consultation">Consultation</MenuItem>
                      <MenuItem value="follow-up">Follow-up</MenuItem>
                      <MenuItem value="check-up">Check-up</MenuItem>
                      <MenuItem value="emergency">Emergency</MenuItem>
                    </Select>
                  </FormControl>
                </Grid>
              </Grid>
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setBookingDialogOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={() => setBookingDialogOpen(false)}>
            Book Appointment
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default PatientDashboard; 