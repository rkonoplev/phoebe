import React, { useState, useEffect, useRef } from 'react';
import { Box, TextField, Button, Switch, FormControlLabel, Typography, Alert, Snackbar } from '@mui/material';
import { useRouter } from 'next/router';
import { admin } from '../../services/api';

const NewsForm = ({ article: initialArticle }) => {
  const [article, setArticle] = useState({
    title: '',
    body: '',
    teaser: '',
    published: false,
  });
  const [errors, setErrors] = useState({});
  const [submitError, setSubmitError] = useState('');
  const [showUndo, setShowUndo] = useState(false);
  const [pendingData, setPendingData] = useState(null);
  const router = useRouter();
  const isEditMode = !!initialArticle;
  const saveTimerRef = useRef(null);

  useEffect(() => {
    if (initialArticle) {
      // Map initialArticle fields to frontend state
      setArticle({
        ...initialArticle,
        body: initialArticle.body || '',
        teaser: initialArticle.teaser || '',
      });
    }
  }, [initialArticle]);

  const validate = () => {
    let tempErrors = {};
    tempErrors.title = article.title.length >= 5 && article.title.length <= 50 ? '' : 'Title must be between 5 and 50 characters.';
    if (!article.title) tempErrors.title = 'Title is required.';

    tempErrors.teaser = article.teaser.length >= 10 && article.teaser.length <= 250 ? '' : 'Teaser must be between 10 and 250 characters.';
    if (!article.teaser) tempErrors.teaser = 'Teaser is required.';

    tempErrors.body = article.body.length >= 20 ? '' : 'Body must be at least 20 characters long.';
    if (!article.body) tempErrors.body = 'Body is required.';

    setErrors(tempErrors);
    return Object.values(tempErrors).every(x => x === '');
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setArticle(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitError('');

    if (validate()) {
      setPendingData({ ...article });
      setShowUndo(true);
      
      saveTimerRef.current = setTimeout(async () => {
        try {
          if (isEditMode) {
            await admin.updateNews(article.id, article);
          } else {
            await admin.createNews(article);
          }
          router.push('/admin/news');
        } catch (error) {
          console.error('Failed to save article:', error);
          setSubmitError('Failed to save article. Please check your input and try again.');
          setShowUndo(false);
          setPendingData(null);
        }
      }, 5000);
    }
  };

  const handleUndo = () => {
    if (saveTimerRef.current) {
      clearTimeout(saveTimerRef.current);
      saveTimerRef.current = null;
    }
    setShowUndo(false);
    setPendingData(null);
  };

  useEffect(() => {
    return () => {
      if (saveTimerRef.current) {
        clearTimeout(saveTimerRef.current);
      }
    };
  }, []);

  return (
    <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
      <Typography variant="h4">{isEditMode ? 'Edit News' : 'Create News'}</Typography>
      <TextField
        name="title"
        label="Title"
        value={article.title}
        onChange={handleChange}
        onBlur={validate}
        required
        fullWidth
        error={!!errors.title}
        helperText={errors.title}
      />
      <TextField
        name="teaser"
        label="Teaser"
        value={article.teaser}
        onChange={handleChange}
        onBlur={validate}
        multiline
        rows={3}
        fullWidth
        required
        error={!!errors.teaser}
        helperText={errors.teaser || "Supports HTML content, including image links (e.g., <img src='...' />) and YouTube embeds."} // Added helperText
      />
      <TextField
        name="body"
        label="Body"
        value={article.body}
        onChange={handleChange}
        onBlur={validate}
        multiline
        rows={10}
        fullWidth
        required
        error={!!errors.body}
        helperText={errors.body || "Supports HTML content, including image links (e.g., <img src='...' />) and YouTube embeds."}
      />
      <FormControlLabel
        control={<Switch name="published" checked={article.published} onChange={handleChange} />}
        label="Published"
      />
      {submitError && <Alert severity="error" sx={{ width: '100%', mt: 2 }}>{submitError}</Alert>}
      <Box>
        <Button type="submit" variant="contained" color="primary" disabled={showUndo}>
          {isEditMode ? 'Update' : 'Create'}
        </Button>
        <Button variant="outlined" onClick={() => router.push('/admin/news')} sx={{ ml: 2 }} disabled={showUndo}>
          Cancel
        </Button>
      </Box>
      
      <Snackbar
        open={showUndo}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
        message="Saving in 5 seconds..."
        action={
          <Button color="primary" size="small" onClick={handleUndo}>
            UNDO
          </Button>
        }
      />
    </Box>
  );
};

export default NewsForm;
