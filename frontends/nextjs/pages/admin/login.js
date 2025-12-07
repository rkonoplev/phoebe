import React, { useState } from 'react';
import { Container, Box, TextField, Button, Typography, Alert } from '@mui/material';
import { useRouter } from 'next/router';
import { useAuth } from '../../context/AuthContext';

const LoginPage = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [errors, setErrors] = useState({});
  const [submitError, setSubmitError] = useState('');
  const router = useRouter();
  const { login } = useAuth();

  const validate = () => {
    let tempErrors = {};
    tempErrors.username = username.length >= 3 && username.length <= 50 ? '' : 'Username must be between 3 and 50 characters.';
    if (!username) tempErrors.username = 'Username is required.';

    tempErrors.password = password.length >= 8 ? '' : 'Password must be at least 8 characters long.'; // Adjusted from 6 to 8
    if (!password) tempErrors.password = 'Password is required.';

    setErrors(tempErrors);
    return Object.values(tempErrors).every(x => x === '');
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    setSubmitError('');

    if (validate()) {
      const result = await login(username, password);

      if (result === true) {
        router.push('/admin'); 
      } else if (result?.error) {
        setSubmitError(result.error);
      } else {
        setSubmitError('Invalid username or password. Please try again.');
      }
    }
  };

  return (
    <Container maxWidth="xs">
      <Box
        sx={{
          marginTop: 8,
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
        }}
      >
        <Typography component="h1" variant="h5">
          Admin Sign In
        </Typography>
        <Box component="form" onSubmit={handleLogin} noValidate sx={{ mt: 1 }}>
          <TextField
            margin="normal"
            required
            fullWidth
            id="username"
            label="Username"
            name="username"
            autoComplete="username"
            autoFocus
            value={username}
            onChange={(e) => { setUsername(e.target.value); validate(); }}
            onBlur={validate}
            error={!!errors.username}
            helperText={errors.username}
          />
          <TextField
            margin="normal"
            required
            fullWidth
            name="password"
            label="Password"
            type="password"
            id="password"
            autoComplete="current-password"
            value={password}
            onChange={(e) => { setPassword(e.target.value); validate(); }}
            onBlur={validate}
            error={!!errors.password}
            helperText={errors.password}
          />
          {submitError && <Alert severity="error" sx={{ width: '100%', mt: 2 }}>{submitError}</Alert>}
          <Button
            type="submit"
            fullWidth
            variant="contained"
            sx={{ mt: 3, mb: 2 }}
          >
            Sign In
          </Button>
        </Box>
      </Box>
    </Container>
  );
};

export default LoginPage;
