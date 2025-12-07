import React, { useState, useEffect } from 'react';
import { useRouter } from 'next/router';
import AdminRoute from '../../../components/AdminRoute';
import { admin } from '../../../services/api';
import {
  Container, TextField, Button, Typography, Box, FormControl,
  InputLabel, Select, MenuItem, FormControlLabel, Checkbox, Alert
} from '@mui/material';

const NewUserPage = () => {
  const router = useRouter();
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    active: true,
    roleIds: []
  });
  const [roles, setRoles] = useState([]);
  const [errors, setErrors] = useState({});
  const [submitError, setSubmitError] = useState('');

  useEffect(() => {
    fetchRoles();
  }, []);

  const fetchRoles = async () => {
    try {
      const response = await admin.getRoles();
      setRoles(response.data);
    } catch (error) {
      console.error('Failed to fetch roles:', error);
    }
  };

  const validate = () => {
    const newErrors = {};
    if (!formData.username || formData.username.length < 3 || formData.username.length > 100) {
      newErrors.username = 'Username must be between 3 and 100 characters';
    }
    if (!formData.email || !/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = 'Valid email is required';
    }
    if (!formData.password || formData.password.length < 8) {
      newErrors.password = 'Password must be at least 8 characters';
    }
    if (!formData.roleIds || formData.roleIds.length === 0) {
      newErrors.roleIds = 'At least one role must be selected';
    }
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitError('');

    if (!validate()) return;

    try {
      await admin.createUser(formData);
      router.push('/admin/users');
    } catch (error) {
      console.error('Failed to create user:', error);
      setSubmitError(error.response?.data?.message || 'Failed to create user');
    }
  };

  return (
    <Container maxWidth="md">
      <Box sx={{ mt: 4 }}>
        <Typography variant="h4" gutterBottom>Create New User</Typography>
        <Box component="form" onSubmit={handleSubmit} sx={{ mt: 3 }}>
          <TextField
            fullWidth
            label="Username"
            value={formData.username}
            onChange={(e) => setFormData({ ...formData, username: e.target.value })}
            error={!!errors.username}
            helperText={errors.username}
            margin="normal"
            required
          />
          <TextField
            fullWidth
            label="Email"
            type="email"
            value={formData.email}
            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
            error={!!errors.email}
            helperText={errors.email}
            margin="normal"
            required
          />
          <TextField
            fullWidth
            label="Password"
            type="password"
            value={formData.password}
            onChange={(e) => setFormData({ ...formData, password: e.target.value })}
            error={!!errors.password}
            helperText={errors.password}
            margin="normal"
            required
          />
          <FormControl fullWidth margin="normal" error={!!errors.roleIds}>
            <InputLabel>Roles</InputLabel>
            <Select
              multiple
              value={formData.roleIds}
              onChange={(e) => setFormData({ ...formData, roleIds: e.target.value })}
              label="Roles"
            >
              {roles.map((role) => (
                <MenuItem key={role.id} value={role.id}>{role.name}</MenuItem>
              ))}
            </Select>
            {errors.roleIds && <Typography color="error" variant="caption">{errors.roleIds}</Typography>}
          </FormControl>
          <FormControlLabel
            control={
              <Checkbox
                checked={formData.active}
                onChange={(e) => setFormData({ ...formData, active: e.target.checked })}
              />
            }
            label="Active"
          />
          {submitError && <Alert severity="error" sx={{ mt: 2 }}>{submitError}</Alert>}
          <Box sx={{ mt: 3, display: 'flex', gap: 2 }}>
            <Button type="submit" variant="contained" color="primary">Create User</Button>
            <Button variant="outlined" onClick={() => router.push('/admin/users')}>Cancel</Button>
          </Box>
        </Box>
      </Box>
    </Container>
  );
};

export default () => (
  <AdminRoute>
    <NewUserPage />
  </AdminRoute>
);
