# HealthFirst Patient Portal & Provider Dashboard

A comprehensive healthcare platform built with React and Material-UI that provides both patient and provider interfaces for appointment management, availability scheduling, and healthcare services.

## 🏥 Features

### Patient Features
- **Secure Authentication**: Login and registration with email validation
- **Provider Search**: Find healthcare providers by specialty, location, and availability
- **Appointment Booking**: One-click booking with confirmation modals
- **Appointment Management**: View, reschedule, and cancel appointments
- **Telemedicine Integration**: Join virtual appointments with video call links
- **Medical Records**: Access to personal health information
- **Prescription Management**: Track prescription refills and reminders

### Provider Features
- **Availability Management**: Interactive calendar for scheduling availability slots
- **Appointment Management**: View and manage patient appointments
- **Patient Management**: Access to patient information and history
- **Revenue Tracking**: Monitor practice revenue and financial metrics
- **Practice Analytics**: Dashboard with key performance indicators

### Unified Dashboard
- **Role-based Views**: Different interfaces for patients and providers
- **Real-time Notifications**: Alerts for appointments, cancellations, and updates
- **Quick Actions**: Easy access to common tasks
- **Responsive Design**: Works seamlessly on desktop and mobile devices

## 🚀 Getting Started

### Prerequisites
- Node.js (v16 or higher)
- npm or yarn

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd health-first-client/patient-portal/my-app
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm start
```

4. Open [http://localhost:3000](http://localhost:3000) to view it in the browser.

## 📱 Usage

### For Patients

1. **Registration/Login**: Create an account or sign in with existing credentials
2. **Find Providers**: Use search filters to find healthcare providers
3. **Book Appointments**: Select available time slots and book appointments
4. **Manage Appointments**: View upcoming appointments and make changes
5. **Access Medical Records**: View personal health information

### For Providers

1. **Login**: Sign in with provider credentials (use email containing "provider")
2. **Manage Availability**: Set up available time slots in the calendar
3. **View Appointments**: See all scheduled appointments
4. **Patient Management**: Access patient information and history
5. **Track Metrics**: Monitor practice performance and revenue

## 🛠️ Technology Stack

- **Frontend**: React 19.1.0
- **UI Framework**: Material-UI (MUI) 7.2.0
- **Routing**: React Router DOM 7.7.1
- **Icons**: Material Icons
- **Styling**: Emotion (CSS-in-JS)
- **Date/Time**: MUI X Date Pickers
- **Data Grid**: MUI X Data Grid
- **Charts**: MUI X Charts

## 📁 Project Structure

```
src/
├── components/
│   ├── dashboard/
│   │   ├── ProviderDashboard.js
│   │   ├── PatientDashboard.js
│   │   └── UnifiedDashboard.js
│   ├── PatientLogin.js
│   └── PatientRegistration.js
├── App.js
├── App.css
└── index.js
```

## 🎨 UI/UX Features

### Design Principles
- **Clean & Modern**: Minimalist design with clear visual hierarchy
- **Accessible**: WCAG compliant with proper ARIA labels and keyboard navigation
- **Responsive**: Mobile-first design that works on all screen sizes
- **Intuitive**: User-friendly interface with clear call-to-actions

### Color Scheme
- **Primary**: Blue (#1976d2) - Trust and professionalism
- **Secondary**: Red (#dc004e) - Urgency and attention
- **Success**: Green (#2e7d32) - Positive actions and confirmations
- **Warning**: Orange (#ed6c02) - Cautions and pending states
- **Error**: Red (#d32f2f) - Errors and cancellations

## 🔒 Security Features

### Authentication
- Email validation with regex patterns
- Password strength requirements
- Secure form handling with input sanitization
- No plain-text password storage

### Data Protection
- Role-based access control
- Masked patient data for providers
- Secure token handling (placeholder for API integration)
- HTTPS enforcement

## 📊 Mock Data

The application currently uses mock data for demonstration purposes:

### Provider Data
- Sample availability slots
- Mock appointment bookings
- Revenue metrics
- Patient waitlist

### Patient Data
- Sample healthcare providers
- Mock appointments
- Medical records
- Prescription information

## 🔧 API Integration Points

The application is designed for easy API integration. Key integration points include:

### Authentication
```javascript
// TODO: Replace with actual API calls
const handleLogin = async (credentials) => {
  // API call to authenticate user
  const response = await api.login(credentials);
  // Handle response and set authentication state
};
```

### Appointment Management
```javascript
// TODO: Replace with actual API calls
const bookAppointment = async (appointmentData) => {
  // API call to book appointment
  const response = await api.bookAppointment(appointmentData);
  // Handle response and update UI
};
```

### Availability Management
```javascript
// TODO: Replace with actual API calls
const updateAvailability = async (slotData) => {
  // API call to update provider availability
  const response = await api.updateAvailability(slotData);
  // Handle response and refresh calendar
};
```

## 🧪 Testing

### Validation Testing
- Email format validation
- Password strength requirements
- Form field validation
- Date and time validation

### User Flow Testing
- Login/registration flow
- Appointment booking process
- Provider availability management
- Dashboard navigation

### Responsive Testing
- Desktop layout (1200px+)
- Tablet layout (768px - 1199px)
- Mobile layout (320px - 767px)

## 🚀 Deployment

### Build for Production
```bash
npm run build
```

### Environment Variables
Create a `.env` file for environment-specific configurations:
```
REACT_APP_API_URL=your-api-endpoint
REACT_APP_ENVIRONMENT=production
```

## 📈 Future Enhancements

### Planned Features
- **Real-time Chat**: Provider-patient messaging
- **Payment Integration**: Secure payment processing
- **Video Conferencing**: Built-in telemedicine platform
- **AI-powered Recommendations**: Smart provider suggestions
- **Mobile App**: Native iOS and Android applications
- **Analytics Dashboard**: Advanced reporting and insights

### Technical Improvements
- **State Management**: Redux or Zustand for complex state
- **TypeScript**: Type safety and better development experience
- **Testing Framework**: Jest and React Testing Library
- **CI/CD Pipeline**: Automated testing and deployment
- **Performance Optimization**: Code splitting and lazy loading

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Contact the development team
- Check the documentation

## 🙏 Acknowledgments

- Material-UI team for the excellent component library
- React team for the amazing framework
- Healthcare professionals for domain expertise and feedback

---

**Note**: This is a static UI prototype. Backend integration and real data persistence are not implemented. All data shown is mock data for demonstration purposes.
