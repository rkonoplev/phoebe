import React, { useState, useEffect } from 'react';
import { Box, TextField, Button, Switch, FormControlLabel, Typography, Alert, Snackbar } from '@mui/material';
import { useRouter } from 'next/router';
import { admin } from '../../services/api';
import useUndoSave from '../../hooks/useUndoSave';

const NewsForm = ({ article: initialArticle }) => {
  const [article, setArticle] = useState({
    title: '',
    body: '',
    teaser: '',
    published: false,
  });
  const [errors, setErrors] = useState({});
  const router = useRouter();
  const isEditMode = !!initialArticle;
  
  const saveFunction = async (data) => {
    if (isEditMode) {
      await admin.updateNews(data.id, data);
    } else {
      await admin.createNews(data);
    }
    router.push('/admin/news');
  };
  
  const { showUndo, error: submitError, executeSave, undoSave, isProcessing } = useUndoSave(saveFunction);

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

  const wrapSelection = (fieldName, openTag, closeTag) => {
    const textarea = document.querySelector(`textarea[name="${fieldName}"]`);
    if (!textarea) return;
    
    const start = textarea.selectionStart;
    const end = textarea.selectionEnd;
    const selectedText = article[fieldName].substring(start, end);
    
    if (selectedText) {
      const before = article[fieldName].substring(0, start);
      const after = article[fieldName].substring(end);
      const newValue = before + openTag + selectedText + closeTag + after;
      
      setArticle(prev => ({ ...prev, [fieldName]: newValue }));
      
      setTimeout(() => {
        textarea.focus();
        textarea.setSelectionRange(start + openTag.length, end + openTag.length);
      }, 0);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (validate()) {
      await executeSave(article);
    }
  };

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
      <Box>
        <Box sx={{ mb: 1, display: 'flex', gap: 1 }}>
          <Button size="small" variant="outlined" onClick={() => wrapSelection('teaser', '<strong>', '</strong>')}>Bold</Button>
          <Button size="small" variant="outlined" onClick={() => wrapSelection('teaser', '<em>', '</em>')}>Italic</Button>
          <Button size="small" variant="outlined" onClick={() => wrapSelection('teaser', '<u>', '</u>')}>Underline</Button>
        </Box>
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
          helperText={errors.teaser || "Supports HTML content, including image links (e.g., <img src='...' />) and YouTube embeds."}
        />
      </Box>
      <Box>
        <Box sx={{ mb: 1, display: 'flex', gap: 1 }}>
          <Button size="small" variant="outlined" onClick={() => wrapSelection('body', '<strong>', '</strong>')}>Bold</Button>
          <Button size="small" variant="outlined" onClick={() => wrapSelection('body', '<em>', '</em>')}>Italic</Button>
          <Button size="small" variant="outlined" onClick={() => wrapSelection('body', '<u>', '</u>')}>Underline</Button>
          <Button size="small" variant="outlined" onClick={() => wrapSelection('body', '<p>', '</p>')}>Paragraph</Button>
        </Box>
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
      </Box>
      <FormControlLabel
        control={<Switch name="published" checked={article.published} onChange={handleChange} />}
        label="Published"
      />
      {submitError && <Alert severity="error" sx={{ width: '100%', mt: 2 }}>{submitError}</Alert>}
      <Box>
        <Button type="submit" variant="contained" color="primary" disabled={isProcessing}>
          {isEditMode ? 'Update' : 'Create'}
        </Button>
        <Button variant="outlined" onClick={() => router.push('/admin/news')} sx={{ ml: 2 }} disabled={isProcessing}>
          Cancel
        </Button>
      </Box>
      
      <Snackbar
        open={showUndo}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
        message="Saving in 5 seconds..."
        action={
          <Button color="primary" size="small" onClick={undoSave}>
            UNDO
          </Button>
        }
      />
    </Box>
  );
};

export default NewsForm;
