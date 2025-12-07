import React, { useState, useEffect } from 'react';
import { Box, TextField, Button, Typography, Alert } from '@mui/material';
import { useRouter } from 'next/router';
import { admin } from '../../services/api';

const TermForm = ({ term: initialTerm }) => {
  const [term, setTerm] = useState({ name: '', vocabulary: '' });
  const [errors, setErrors] = useState({});
  const [submitError, setSubmitError] = useState('');
  const router = useRouter();
  const isEditMode = !!initialTerm;

  useEffect(() => {
    if (initialTerm) {
      setTerm(initialTerm);
    }
  }, [initialTerm]);

  const validate = () => {
    let tempErrors = {};
    tempErrors.name = term.name.length >= 2 && term.name.length <= 100 ? '' : 'Name must be between 2 and 100 characters.';
    if (!term.name) tempErrors.name = 'Name is required.';

    // Assuming 'vocabulary' maps to a field with max 50 chars on backend, e.g., Role.name or similar string field
    tempErrors.vocabulary = term.vocabulary.length >= 2 && term.vocabulary.length <= 50 ? '' : 'Vocabulary must be between 2 and 50 characters.';
    if (!term.vocabulary) tempErrors.vocabulary = 'Vocabulary is required.';

    setErrors(tempErrors);
    return Object.values(tempErrors).every(x => x === '');
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setTerm(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitError('');

    if (validate()) {
      try {
        if (isEditMode) {
          await admin.updateTerm(term.id, term);
        } else {
          await admin.createTerm(term);
        }
        router.push('/admin/taxonomy');
      } catch (error) {
        console.error('Failed to save term:', error);
        setSubmitError('Failed to save term. Please check your input and try again.');
      }
    }
  };

  return (
    <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
      <Typography variant="h4">{isEditMode ? 'Edit Term' : 'Create Term'}</Typography>
      <TextField
        name="name"
        label="Term Name"
        value={term.name}
        onChange={handleChange}
        onBlur={validate}
        required
        fullWidth
        error={!!errors.name}
        helperText={errors.name}
      />
      <TextField
        name="vocabulary"
        label="Vocabulary"
        value={term.vocabulary}
        onChange={handleChange}
        onBlur={validate}
        required
        fullWidth
        error={!!errors.vocabulary}
        helperText={errors.vocabulary}
      />
      {submitError && <Alert severity="error" sx={{ width: '100%', mt: 2 }}>{submitError}</Alert>}
      <Box>
        <Button type="submit" variant="contained" color="primary">
          {isEditMode ? 'Update' : 'Create'}
        </Button>
        <Button variant="outlined" onClick={() => router.push('/admin/taxonomy')} sx={{ ml: 2 }}>
          Cancel
        </Button>
      </Box>
    </Box>
  );
};

export default TermForm;
