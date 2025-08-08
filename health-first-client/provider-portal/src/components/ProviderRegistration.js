import React, { useState } from 'react';
import {
  Box, Paper, TextField, Button, Typography, Link, Alert, CircularProgress,
  InputAdornment, IconButton, Container, useTheme, useMediaQuery, Snackbar,
  Grid, FormControl, InputLabel, Select, MenuItem, Stepper, Step, StepLabel, FormHelperText
} from '@mui/material';
import {
  Visibility, VisibilityOff, Person, Email, Phone, Business, Badge,
  Work, LocationOn, Lock, ArrowBack, ArrowForward, CheckCircle
} from '@mui/icons-material';
import './ProviderLogin.css';

const steps = ['Personal Info', 'Professional Info', 'Clinic Address', 'Security'];

const specializations = [
  'Cardiology', 'Dermatology', 'Endocrinology', 'Family Medicine',
  'Gastroenterology', 'General Surgery', 'Internal Medicine', 'Neurology',
  'Obstetrics & Gynecology', 'Oncology', 'Ophthalmology', 'Orthopedics',
  'Pediatrics', 'Psychiatry', 'Radiology', 'Urology', 'Other'
];

const ProviderRegistration = ({ onSwitchToLogin }) => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
  
  const [formData, setFormData] = useState({
    firstName: '', lastName: '', email: '', phone: '',
    specialization: '', medicalLicenseNumber: '', yearsOfExperience: '',
    streetAddress: '', city: '', state: '', zipCode: '',
    password: '', confirmPassword: ''
  });
  
  const [activeStep, setActiveStep] = useState(0);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});
  const [success, setSuccess] = useState(false);
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'info' });

  const validateEmail = (email) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  const validatePhone = (phone) => /^[\+]?[1-9][\d]{0,15}$/.test(phone.replace(/[\s\-\(\)]/g, ''));
  const validateLicenseNumber = (license) => /^[A-Z0-9]{6,12}$/.test(license.toUpperCase());
  const validatePassword = (password) => {
    return password.length >= 8 && /[A-Z]/.test(password) && /[a-z]/.test(password) && 
           /\d/.test(password) && /[!@#$%^&*(),.?":{}|<>]/.test(password);
  };
  const validateZipCode = (zipCode) => /^\d{5}(-\d{4})?$/.test(zipCode);

  const validateStep = (step) => {
    const newErrors = {};
    
    switch (step) {
      case 0:
        if (!formData.firstName.trim()) newErrors.firstName = 'First name is required';
        else if (formData.firstName.length < 2) newErrors.firstName = 'First name must be at least 2 characters';
        else if (formData.firstName.length > 50) newErrors.firstName = 'First name must be less than 50 characters';
        
        if (!formData.lastName.trim()) newErrors.lastName = 'Last name is required';
        else if (formData.lastName.length < 2) newErrors.lastName = 'Last name must be at least 2 characters';
        else if (formData.lastName.length > 50) newErrors.lastName = 'Last name must be less than 50 characters';
        
        if (!formData.email) newErrors.email = 'Email is required';
        else if (!validateEmail(formData.email)) newErrors.email = 'Please enter a valid email address';
        
        if (!formData.phone) newErrors.phone = 'Phone number is required';
        else if (!validatePhone(formData.phone)) newErrors.phone = 'Please enter a valid phone number';
        break;
        
      case 1:
        if (!formData.specialization) newErrors.specialization = 'Specialization is required';
        else if (formData.specialization.length < 3) newErrors.specialization = 'Specialization must be at least 3 characters';
        else if (formData.specialization.length > 100) newErrors.specialization = 'Specialization must be less than 100 characters';
        
        if (!formData.medicalLicenseNumber) newErrors.medicalLicenseNumber = 'Medical license number is required';
        else if (!validateLicenseNumber(formData.medicalLicenseNumber)) newErrors.medicalLicenseNumber = 'License number must be 6-12 alphanumeric characters';
        
        if (!formData.yearsOfExperience) newErrors.yearsOfExperience = 'Years of experience is required';
        else if (formData.yearsOfExperience < 0 || formData.yearsOfExperience > 50) newErrors.yearsOfExperience = 'Years of experience must be between 0 and 50';
        break;
        
      case 2:
        if (!formData.streetAddress.trim()) newErrors.streetAddress = 'Street address is required';
        else if (formData.streetAddress.length > 200) newErrors.streetAddress = 'Street address must be less than 200 characters';
        
        if (!formData.city.trim()) newErrors.city = 'City is required';
        else if (formData.city.length > 100) newErrors.city = 'City must be less than 100 characters';
        
        if (!formData.state.trim()) newErrors.state = 'State/Province is required';
        else if (formData.state.length > 50) newErrors.state = 'State/Province must be less than 50 characters';
        
        if (!formData.zipCode) newErrors.zipCode = 'ZIP/Postal code is required';
        else if (!validateZipCode(formData.zipCode)) newErrors.zipCode = 'Please enter a valid ZIP/Postal code';
        break;
        
      case 3:
        if (!formData.password) newErrors.password = 'Password is required';
        else if (!validatePassword(formData.password)) newErrors.password = 'Password must be at least 8 characters with uppercase, lowercase, number, and special character';
        
        if (!formData.confirmPassword) newErrors.confirmPassword = 'Please confirm your password';
        else if (formData.password !== formData.confirmPassword) newErrors.confirmPassword = 'Passwords do not match';
        break;
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleInputChange = (field, value) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    if (errors[field]) setErrors(prev => ({ ...prev, [field]: '' }));
  };

  const handleNext = () => {
    if (validateStep(activeStep)) setActiveStep(prev => prev + 1);
  };

  const handleBack = () => setActiveStep(prev => prev - 1);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateStep(activeStep)) return;
    
    setLoading(true);
    try {
      await new Promise(resolve => setTimeout(resolve, 2000));
      const random = Math.random();
      
      if (random < 0.1) throw new Error('Email already exists');
      else if (random < 0.15) throw new Error('License number already registered');
      else if (random < 0.2) throw new Error('Network error');
      
      setSuccess(true);
      setSnackbar({ open: true, message: 'Registration successful! Redirecting to login...', severity: 'success' });
      setTimeout(() => onSwitchToLogin(), 2000);
    } catch (error) {
      setSnackbar({ open: true, message: error.message, severity: 'error' });
    } finally {
      setLoading(false);
    }
  };

  const renderStepContent = (step) => {
    switch (step) {
      case 0:
        return (
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6}>
              <TextField fullWidth label="First Name" value={formData.firstName}
                onChange={(e) => handleInputChange('firstName', e.target.value)} error={!!errors.firstName}
                helperText={errors.firstName} required disabled={loading}
                InputProps={{ startAdornment: <InputAdornment position="start"><Person color="action" /></InputAdornment> }} />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField fullWidth label="Last Name" value={formData.lastName}
                onChange={(e) => handleInputChange('lastName', e.target.value)} error={!!errors.lastName}
                helperText={errors.lastName} required disabled={loading} />
            </Grid>
            <Grid item xs={12}>
              <TextField fullWidth label="Email Address" type="email" value={formData.email}
                onChange={(e) => handleInputChange('email', e.target.value)} error={!!errors.email}
                helperText={errors.email} required disabled={loading}
                InputProps={{ startAdornment: <InputAdornment position="start"><Email color="action" /></InputAdornment> }} />
            </Grid>
            <Grid item xs={12}>
              <TextField fullWidth label="Phone Number" type="tel" value={formData.phone}
                onChange={(e) => handleInputChange('phone', e.target.value)} error={!!errors.phone}
                helperText={errors.phone} required disabled={loading}
                InputProps={{ startAdornment: <InputAdornment position="start"><Phone color="action" /></InputAdornment> }} />
            </Grid>
          </Grid>
        );
        
      case 1:
        return (
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <FormControl fullWidth error={!!errors.specialization} required>
                <InputLabel>Specialization</InputLabel>
                <Select value={formData.specialization} onChange={(e) => handleInputChange('specialization', e.target.value)}
                  disabled={loading} startAdornment={<InputAdornment position="start"><Work color="action" /></InputAdornment>}>
                  {specializations.map((spec) => <MenuItem key={spec} value={spec}>{spec}</MenuItem>)}
                </Select>
                {errors.specialization && <FormHelperText>{errors.specialization}</FormHelperText>}
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <TextField fullWidth label="Medical License Number" value={formData.medicalLicenseNumber}
                onChange={(e) => handleInputChange('medicalLicenseNumber', e.target.value.toUpperCase())}
                error={!!errors.medicalLicenseNumber} helperText={errors.medicalLicenseNumber} required disabled={loading}
                InputProps={{ startAdornment: <InputAdornment position="start"><Badge color="action" /></InputAdornment> }} />
            </Grid>
            <Grid item xs={12}>
              <TextField fullWidth label="Years of Experience" type="number" value={formData.yearsOfExperience}
                onChange={(e) => handleInputChange('yearsOfExperience', parseInt(e.target.value) || '')}
                error={!!errors.yearsOfExperience} helperText={errors.yearsOfExperience} required
                inputProps={{ min: 0, max: 50 }} disabled={loading} />
            </Grid>
          </Grid>
        );
        
      case 2:
        return (
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <TextField fullWidth label="Street Address" value={formData.streetAddress}
                onChange={(e) => handleInputChange('streetAddress', e.target.value)} error={!!errors.streetAddress}
                helperText={errors.streetAddress} required disabled={loading}
                InputProps={{ startAdornment: <InputAdornment position="start"><LocationOn color="action" /></InputAdornment> }} />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField fullWidth label="City" value={formData.city}
                onChange={(e) => handleInputChange('city', e.target.value)} error={!!errors.city}
                helperText={errors.city} required disabled={loading} />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField fullWidth label="State/Province" value={formData.state}
                onChange={(e) => handleInputChange('state', e.target.value)} error={!!errors.state}
                helperText={errors.state} required disabled={loading} />
            </Grid>
            <Grid item xs={12}>
              <TextField fullWidth label="ZIP/Postal Code" value={formData.zipCode}
                onChange={(e) => handleInputChange('zipCode', e.target.value)} error={!!errors.zipCode}
                helperText={errors.zipCode} required disabled={loading} />
            </Grid>
          </Grid>
        );
        
      case 3:
        return (
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <TextField fullWidth label="Password" type={showPassword ? 'text' : 'password'} value={formData.password}
                onChange={(e) => handleInputChange('password', e.target.value)} error={!!errors.password}
                helperText={errors.password} required disabled={loading}
                InputProps={{
                  startAdornment: <InputAdornment position="start"><Lock color="action" /></InputAdornment>,
                  endAdornment: <InputAdornment position="end">
                    <IconButton onClick={() => setShowPassword(!showPassword)} edge="end" disabled={loading}>
                      {showPassword ? <VisibilityOff /> : <Visibility />}
                    </IconButton>
                  </InputAdornment>
                }} />
            </Grid>
            <Grid item xs={12}>
              <TextField fullWidth label="Confirm Password" type={showConfirmPassword ? 'text' : 'password'}
                value={formData.confirmPassword} onChange={(e) => handleInputChange('confirmPassword', e.target.value)}
                error={!!errors.confirmPassword} helperText={errors.confirmPassword} required disabled={loading}
                InputProps={{
                  endAdornment: <InputAdornment position="end">
                    <IconButton onClick={() => setShowConfirmPassword(!showConfirmPassword)} edge="end" disabled={loading}>
                      {showConfirmPassword ? <VisibilityOff /> : <Visibility />}
                    </IconButton>
                  </InputAdornment>
                }} />
            </Grid>
          </Grid>
        );
        
      default: return null;
    }
  };

  return (
    <Container maxWidth="md" className="provider-registration-container">
      <Box sx={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', py: 4 }}>
        <Paper elevation={3} className="provider-registration-paper" sx={{ p: isMobile ? 3 : 4, width: '100%', maxWidth: 600, borderRadius: 2 }}>
          <Box sx={{ textAlign: 'center', mb: 4 }} className="provider-registration-header">
            <Business sx={{ fontSize: 48, color: theme.palette.primary.main, mb: 2 }} />
            <Typography variant="h4" component="h1" gutterBottom>Provider Registration</Typography>
            <Typography variant="body2" color="text.secondary">Create your healthcare professional profile</Typography>
          </Box>

          <Stepper activeStep={activeStep} sx={{ mb: 4 }}>
            {steps.map((label) => <Step key={label}><StepLabel>{label}</StepLabel></Step>)}
          </Stepper>

          <Box component="form" onSubmit={handleSubmit} noValidate className="provider-registration-form">
            {renderStepContent(activeStep)}

            <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 4 }}>
              <Button disabled={activeStep === 0 || loading} onClick={handleBack} startIcon={<ArrowBack />} className="provider-registration-button">
                Back
              </Button>
              
              {activeStep === steps.length - 1 ? (
                <Button type="submit" variant="contained" disabled={loading}
                  startIcon={loading ? <CircularProgress size={20} color="inherit" /> : <CheckCircle />}
                  className={`provider-registration-button ${loading ? 'provider-registration-loading' : ''}`}
                  sx={{ py: 1.5, fontSize: '1.1rem', fontWeight: 600 }}>
                  {loading ? 'Creating Account...' : 'Create Account'}
                </Button>
              ) : (
                <Button variant="contained" onClick={handleNext} endIcon={<ArrowForward />} className="provider-registration-button">
                  Next
                </Button>
              )}
            </Box>

            {success && <Alert severity="success" sx={{ mt: 2 }} className="provider-registration-success">Registration successful! Redirecting to login...</Alert>}
          </Box>

          <Box sx={{ textAlign: 'center', mt: 4 }}>
            <Typography variant="body2" color="text.secondary">
              Already have an account?{' '}
              <Link component="button" variant="body2" onClick={onSwitchToLogin} className="provider-registration-link" sx={{ textDecoration: 'none' }}>
                Sign in here
              </Link>
            </Typography>
          </Box>
        </Paper>
      </Box>

      <Snackbar open={snackbar.open} autoHideDuration={6000} onClose={() => setSnackbar(prev => ({ ...prev, open: false }))} anchorOrigin={{ vertical: 'top', horizontal: 'center' }}>
        <Alert onClose={() => setSnackbar(prev => ({ ...prev, open: false }))} severity={snackbar.severity} sx={{ width: '100%' }}>
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Container>
  );
};

export default ProviderRegistration; 