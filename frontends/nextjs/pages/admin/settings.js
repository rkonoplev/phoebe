import React, { useState, useEffect } from 'react';
import AdminRoute from '../../components/AdminRoute';
import { admin } from '../../services/api';
import {
  Container, TextField, Button, Typography, Box, Alert, Paper
} from '@mui/material';

const SiteSettingsPage = () => {
  const [formData, setFormData] = useState({
    siteTitle: '',
    metaDescription: '',
    metaKeywords: '',
    siteUrl: '',
    logoUrl: '',
    headerHtml: '',
    footerHtml: '',
    mainMenuTermIds: ''
  });
  const [errors, setErrors] = useState({});
  const [submitError, setSubmitError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchSettings();
  }, []);

  const fetchSettings = async () => {
    try {
      const response = await admin.getChannelSettings();
      setFormData(response.data);
      setLoading(false);
    } catch (error) {
      console.error('Failed to fetch settings:', error);
      setLoading(false);
    }
  };

  const validate = () => {
    const newErrors = {};
    if (formData.siteTitle && formData.siteTitle.length > 255) {
      newErrors.siteTitle = 'Site title must not exceed 255 characters';
    }
    if (formData.metaDescription && formData.metaDescription.length > 500) {
      newErrors.metaDescription = 'Meta description must not exceed 500 characters';
    }
    if (formData.metaKeywords && formData.metaKeywords.length > 500) {
      newErrors.metaKeywords = 'Meta keywords must not exceed 500 characters';
    }
    if (formData.siteUrl && formData.siteUrl.length > 255) {
      newErrors.siteUrl = 'Site URL must not exceed 255 characters';
    }
    if (formData.logoUrl && formData.logoUrl.length > 500) {
      newErrors.logoUrl = 'Logo URL must not exceed 500 characters';
    }
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitError('');
    setSuccessMessage('');

    if (!validate()) return;

    try {
      await admin.updateChannelSettings(formData);
      setSuccessMessage('Settings updated successfully!');
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (error) {
      console.error('Failed to update settings:', error);
      setSubmitError(error.response?.data?.message || 'Failed to update settings');
    }
  };

  if (loading) return <Typography>Loading...</Typography>;

  return (
    <Container maxWidth="md">
      <Box sx={{ mt: 4 }}>
        <Typography variant="h4" gutterBottom>Site Settings</Typography>
        <Paper sx={{ p: 3, mt: 3 }}>
          <Box component="form" onSubmit={handleSubmit}>
            <Typography variant="h6" gutterBottom sx={{ mt: 2 }}>Basic Information</Typography>
            
            <TextField
              fullWidth
              label="Site Title"
              value={formData.siteTitle || ''}
              onChange={(e) => setFormData({ ...formData, siteTitle: e.target.value })}
              error={!!errors.siteTitle}
              helperText={errors.siteTitle || 'Main title of your website'}
              margin="normal"
            />

            <TextField
              fullWidth
              label="Site URL"
              value={formData.siteUrl || ''}
              onChange={(e) => setFormData({ ...formData, siteUrl: e.target.value })}
              error={!!errors.siteUrl}
              helperText={errors.siteUrl || 'Full URL of your website (e.g., https://example.com)'}
              margin="normal"
            />

            <TextField
              fullWidth
              label="Logo URL"
              value={formData.logoUrl || ''}
              onChange={(e) => setFormData({ ...formData, logoUrl: e.target.value })}
              error={!!errors.logoUrl}
              helperText={errors.logoUrl || 'URL to your site logo image'}
              margin="normal"
            />

            <Typography variant="h6" gutterBottom sx={{ mt: 3 }}>SEO Settings</Typography>

            <TextField
              fullWidth
              label="Meta Description"
              multiline
              rows={3}
              value={formData.metaDescription || ''}
              onChange={(e) => setFormData({ ...formData, metaDescription: e.target.value })}
              error={!!errors.metaDescription}
              helperText={errors.metaDescription || 'Brief description for search engines (max 500 chars)'}
              margin="normal"
            />

            <TextField
              fullWidth
              label="Meta Keywords"
              value={formData.metaKeywords || ''}
              onChange={(e) => setFormData({ ...formData, metaKeywords: e.target.value })}
              error={!!errors.metaKeywords}
              helperText={errors.metaKeywords || 'Comma-separated keywords for SEO'}
              margin="normal"
            />

            <Typography variant="h6" gutterBottom sx={{ mt: 3 }}>Custom HTML</Typography>

            <TextField
              fullWidth
              label="Header HTML"
              multiline
              rows={4}
              value={formData.headerHtml || ''}
              onChange={(e) => setFormData({ ...formData, headerHtml: e.target.value })}
              helperText="Custom HTML to inject in the header (e.g., analytics scripts)"
              margin="normal"
            />

            <TextField
              fullWidth
              label="Footer HTML"
              multiline
              rows={4}
              value={formData.footerHtml || ''}
              onChange={(e) => setFormData({ ...formData, footerHtml: e.target.value })}
              helperText="Custom HTML for footer content"
              margin="normal"
            />

            <Typography variant="h6" gutterBottom sx={{ mt: 3 }}>Navigation</Typography>

            <TextField
              fullWidth
              label="Main Menu Term IDs"
              value={formData.mainMenuTermIds || ''}
              onChange={(e) => setFormData({ ...formData, mainMenuTermIds: e.target.value })}
              helperText="JSON array of term IDs for main menu (e.g., [1,2,3])"
              margin="normal"
            />

            {submitError && <Alert severity="error" sx={{ mt: 2 }}>{submitError}</Alert>}
            {successMessage && <Alert severity="success" sx={{ mt: 2 }}>{successMessage}</Alert>}

            <Box sx={{ mt: 3 }}>
              <Button type="submit" variant="contained" color="primary" size="large">
                Save Settings
              </Button>
            </Box>
          </Box>
        </Paper>
      </Box>
    </Container>
  );
};

export default () => (
  <AdminRoute>
    <SiteSettingsPage />
  </AdminRoute>
);
