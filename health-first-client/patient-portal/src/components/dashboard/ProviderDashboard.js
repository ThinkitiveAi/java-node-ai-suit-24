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
  FormHelperText,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Tabs,
  Tab,
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
  Add,
  Edit,
  Delete,
  Event,
  LocationOn,
  AccessTime,
  Phone,
  Email,
  ExitToApp,
  ChevronRight,
  LocalHospital,
} from '@mui/icons-material';

const ProviderDashboard = ({ onLogout }) => {
  const navigate = useNavigate();
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [tabValue, setTabValue] = useState(0);
  const [slotDialogOpen, setSlotDialogOpen] = useState(false);
  const [selectedSlot, setSelectedSlot] = useState(null);
  const [notifications] = useState([
    { id: 1, message: 'New appointment request from John Doe', type: 'info', time: '2 min ago' },
    { id: 2, message: 'Appointment cancelled for tomorrow', type: 'warning', time: '1 hour ago' },
    { id: 3, message: 'Payment received for consultation', type: 'success', time: '3 hours ago' },
  ]);

  // Mock data
  const [availabilitySlots] = useState([
    { id: 1, date: '2024-01-15', startTime: '09:00', endTime: '10:00', status: 'available', type: 'Consultation', maxPatients: 1 },
    { id: 2, date: '2024-01-15', startTime: '10:00', endTime: '11:00', status: 'booked', type: 'Follow-up', maxPatients: 1, patient: 'John Doe' },
    { id: 3, date: '2024-01-15', startTime: '11:00', endTime: '12:00', status: 'break', type: 'Break', maxPatients: 0 },
    { id: 4, date: '2024-01-16', startTime: '14:00', endTime: '15:00', status: 'available', type: 'Consultation', maxPatients: 1 },
  ]);

  const [appointments] = useState([
    { id: 1, patient: 'John Doe', date: '2024-01-15', time: '10:00 AM', status: 'confirmed', type: 'Follow-up', contact: '+1-555-0123' },
    { id: 2, patient: 'Jane Smith', date: '2024-01-16', time: '02:00 PM', status: 'pending', type: 'Consultation', contact: '+1-555-0124' },
    { id: 3, patient: 'Mike Johnson', date: '2024-01-17', time: '09:00 AM', status: 'cancelled', type: 'Check-up', contact: '+1-555-0125' },
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

  const handleAddSlot = () => {
    setSelectedSlot(null);
    setSlotDialogOpen(true);
  };

  const handleEditSlot = (slot) => {
    setSelectedSlot(slot);
    setSlotDialogOpen(true);
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'available': return 'success';
      case 'booked': return 'primary';
      case 'break': return 'warning';
      case 'cancelled': return 'error';
      default: return 'default';
    }
  };

  const getStatusLabel = (status) => {
    switch (status) {
      case 'available': return 'Available';
      case 'booked': return 'Booked';
      case 'break': return 'Break';
      case 'cancelled': return 'Cancelled';
      default: return status;
    }
  };

  const drawer = (
    <Box sx={{ width: 250 }}>
      <Box sx={{ p: 2, textAlign: 'center' }}>
        <Avatar sx={{ width: 56, height: 56, mx: 'auto', mb: 1 }}>
          <MedicalServices />
        </Avatar>
        <Typography variant="h6">Dr. Smith</Typography>
        <Chip label="Healthcare Provider" color="primary" size="small" />
      </Box>
      <Divider />
      <List>
        <ListItem button onClick={() => navigate('/dashboard')}>
          <ListItemIcon><Dashboard /></ListItemIcon>
          <ListItemText primary="Main Dashboard" />
        </ListItem>
        <ListItem button>
          <ListItemIcon><CalendarToday /></ListItemIcon>
          <ListItemText primary="Availability" />
        </ListItem>
        <ListItem button>
          <ListItemIcon><People /></ListItemIcon>
          <ListItemText primary="Patients" />
        </ListItem>
        <ListItem button>
          <ListItemIcon><MonetizationOn /></ListItemIcon>
          <ListItemText primary="Revenue" />
        </ListItem>
        <ListItem button>
          <ListItemIcon><LocalHospital /></ListItemIcon>
          <ListItemText primary="Practice Settings" />
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
            Provider Dashboard
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
              Provider Dashboard
            </Typography>
            <Typography variant="body1" color="text.secondary">
              Manage your availability, appointments, and practice.
            </Typography>
          </Box>

          {/* Quick Stats */}
          <Grid container spacing={3} sx={{ mb: 4 }}>
            <Grid item xs={12} sm={6} md={3}>
              <Card>
                <CardContent>
                  <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                    <CalendarToday color="primary" sx={{ mr: 1 }} />
                    <Typography variant="h6">Today's Appointments</Typography>
                  </Box>
                  <Typography variant="h4" color="primary">8</Typography>
                  <Typography variant="body2" color="text.secondary">
                    45 this week
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
                  <Typography variant="h4" color="success.main">$1,200</Typography>
                  <Typography variant="body2" color="text.secondary">
                    $8,500 this week
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
                  <Typography variant="h4" color="warning.main">12</Typography>
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
                    <Schedule color="info" sx={{ mr: 1 }} />
                    <Typography variant="h6">Available Slots</Typography>
                  </Box>
                  <Typography variant="h4" color="info.main">15</Typography>
                  <Typography variant="body2" color="text.secondary">
                    This week
                  </Typography>
                </CardContent>
              </Card>
            </Grid>
          </Grid>

          {/* Main Content Tabs */}
          <Card>
            <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
              <Tabs value={tabValue} onChange={handleTabChange}>
                <Tab label="Availability Calendar" />
                <Tab label="Appointments" />
                <Tab label="Patients" />
              </Tabs>
            </Box>

            <Box sx={{ p: 3 }}>
              {tabValue === 0 && (
                <Box>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                    <Typography variant="h6">Availability Slots</Typography>
                    <Button
                      variant="contained"
                      startIcon={<Add />}
                      onClick={handleAddSlot}
                    >
                      Add Slot
                    </Button>
                  </Box>

                  <Grid container spacing={2}>
                    {availabilitySlots.map((slot) => (
                      <Grid item xs={12} sm={6} md={4} key={slot.id}>
                        <Card>
                          <CardContent>
                            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
                              <Typography variant="h6">{slot.date}</Typography>
                              <Chip 
                                label={getStatusLabel(slot.status)} 
                                color={getStatusColor(slot.status)} 
                                size="small" 
                              />
                            </Box>
                            <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                              {slot.startTime} - {slot.endTime}
                            </Typography>
                            <Typography variant="body2" sx={{ mb: 1 }}>
                              Type: {slot.type}
                            </Typography>
                            {slot.patient && (
                              <Typography variant="body2" color="primary">
                                Patient: {slot.patient}
                              </Typography>
                            )}
                            <Box sx={{ mt: 2, display: 'flex', gap: 1 }}>
                              <Button
                                size="small"
                                startIcon={<Edit />}
                                onClick={() => handleEditSlot(slot)}
                              >
                                Edit
                              </Button>
                              <Button
                                size="small"
                                color="error"
                                startIcon={<Delete />}
                              >
                                Delete
                              </Button>
                            </Box>
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
                    Appointment Management
                  </Typography>
                  <TableContainer component={Paper}>
                    <Table>
                      <TableHead>
                        <TableRow>
                          <TableCell>Patient</TableCell>
                          <TableCell>Date & Time</TableCell>
                          <TableCell>Type</TableCell>
                          <TableCell>Status</TableCell>
                          <TableCell>Contact</TableCell>
                          <TableCell>Actions</TableCell>
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {appointments.map((appointment) => (
                          <TableRow key={appointment.id}>
                            <TableCell>
                              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                <Avatar sx={{ width: 32, height: 32, mr: 1 }}>
                                  <Person />
                                </Avatar>
                                {appointment.patient}
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
                              <Box sx={{ display: 'flex', flexDirection: 'column' }}>
                                <Typography variant="body2" sx={{ display: 'flex', alignItems: 'center' }}>
                                  <Phone sx={{ fontSize: 16, mr: 0.5 }} />
                                  {appointment.contact}
                                </Typography>
                                <Typography variant="body2" sx={{ display: 'flex', alignItems: 'center' }}>
                                  <Email sx={{ fontSize: 16, mr: 0.5 }} />
                                  patient@email.com
                                </Typography>
                              </Box>
                            </TableCell>
                            <TableCell>
                              <Box sx={{ display: 'flex', gap: 1 }}>
                                <Button size="small" variant="outlined">
                                  Reschedule
                                </Button>
                                <Button size="small" color="error" variant="outlined">
                                  Cancel
                                </Button>
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
                    Patient Management
                  </Typography>
                  <Alert severity="info" sx={{ mb: 2 }}>
                    Patient data is masked for privacy. Full details available after authentication.
                  </Alert>
                  <Grid container spacing={2}>
                    {[1, 2, 3, 4, 5, 6].map((patient) => (
                      <Grid item xs={12} sm={6} md={4} key={patient}>
                        <Card>
                          <CardContent>
                            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                              <Avatar sx={{ mr: 2 }}>
                                <Person />
                              </Avatar>
                              <Box>
                                <Typography variant="h6">Patient #{patient}</Typography>
                                <Typography variant="body2" color="text.secondary">
                                  Last visit: 2 weeks ago
                                </Typography>
                              </Box>
                            </Box>
                            <Box sx={{ display: 'flex', gap: 1, mb: 2 }}>
                              <Chip label="Active" color="success" size="small" />
                              <Chip label="Follow-up" color="primary" size="small" />
                            </Box>
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

      {/* Slot Management Dialog */}
      <Dialog open={slotDialogOpen} onClose={() => setSlotDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>
          {selectedSlot ? 'Edit Availability Slot' : 'Add Availability Slot'}
        </DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Date"
                type="date"
                defaultValue={selectedSlot?.date || ''}
                InputLabelProps={{ shrink: true }}
              />
            </Grid>
            <Grid item xs={6}>
              <TextField
                fullWidth
                label="Start Time"
                type="time"
                defaultValue={selectedSlot?.startTime || ''}
                InputLabelProps={{ shrink: true }}
              />
            </Grid>
            <Grid item xs={6}>
              <TextField
                fullWidth
                label="End Time"
                type="time"
                defaultValue={selectedSlot?.endTime || ''}
                InputLabelProps={{ shrink: true }}
              />
            </Grid>
            <Grid item xs={12}>
              <FormControl fullWidth>
                <InputLabel>Appointment Type</InputLabel>
                <Select defaultValue={selectedSlot?.type || 'consultation'}>
                  <MenuItem value="consultation">Consultation</MenuItem>
                  <MenuItem value="follow-up">Follow-up</MenuItem>
                  <MenuItem value="check-up">Check-up</MenuItem>
                  <MenuItem value="emergency">Emergency</MenuItem>
                  <MenuItem value="break">Break</MenuItem>
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Maximum Patients"
                type="number"
                defaultValue={selectedSlot?.maxPatients || 1}
                inputProps={{ min: 0, max: 10 }}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Notes"
                multiline
                rows={3}
                placeholder="Optional notes about this time slot..."
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setSlotDialogOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={() => setSlotDialogOpen(false)}>
            {selectedSlot ? 'Update' : 'Create'} Slot
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default ProviderDashboard; 