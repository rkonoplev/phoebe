import React, { useState, useEffect } from 'react';
import {
    TextField, Button, Box, CircularProgress, Alert,
    Select, MenuItem, InputLabel, FormControl, FormGroup, FormControlLabel, Checkbox, Typography
} from '@mui/material';
import { useRouter } from 'next/router';
import api from '../../../services/api';

const HomePageBlockForm = ({ blockId }) => {
    const [form, setForm] = useState({
        weight: 0,
        blockType: 'NEWS_BLOCK',
        taxonomyTermIds: [],
        newsCount: 10,
        showTeaser: true,
        titleFontSize: '1rem',
        content: ''
    });
    const [allTerms, setAllTerms] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const router = useRouter();
    const isNew = !blockId;

    useEffect(() => {
        const fetchTaxonomyTerms = async () => {
            try {
                const response = await api.get('/admin/terms');
                setAllTerms(response.data);
            } catch (err) {
                console.error("Failed to fetch taxonomy terms", err);
                setError("Could not load taxonomy terms. Please try again.");
            }
        };

        fetchTaxonomyTerms();

        if (!isNew) {
            setLoading(true);
            api.get(`/admin/homepage-blocks/${blockId}`)
                .then(response => {
                    setForm(response.data);
                    setLoading(false);
                })
                .catch(err => {
                    setError('Failed to load block data.');
                    console.error(err);
                    setLoading(false);
                });
        }
    }, [blockId, isNew]);

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setForm(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
    };

    const handleTermChange = (e) => {
        const { value } = e.target;
        setForm(prev => ({
            ...prev,
            taxonomyTermIds: typeof value === 'string' ? value.split(',') : value,
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError(null);

        const apiCall = isNew
            ? api.post('/admin/homepage-blocks', form)
            : api.put(`/admin/homepage-blocks/${blockId}`, form);

        try {
            await apiCall;
            router.push('/admin/homepage-blocks');
        } catch (err) {
            setError('Failed to save the block. Check the console for details.');
            console.error(err);
            setLoading(false);
        }
    };

    if (loading && !form.id) {
        return <CircularProgress />;
    }

    return (
        <Box component="form" onSubmit={handleSubmit} sx={{ mt: 4 }}>
            <Typography variant="h5" sx={{ mb: 2 }}>
                {isNew ? 'Create New Block' : 'Edit Block'}
            </Typography>

            {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

            <TextField
                name="weight"
                label="Weight (Order)"
                type="number"
                value={form.weight}
                onChange={handleChange}
                fullWidth
                required
                sx={{ mb: 2 }}
            />

            <FormControl fullWidth sx={{ mb: 2 }}>
                <InputLabel>Block Type</InputLabel>
                <Select
                    name="blockType"
                    value={form.blockType}
                    label="Block Type"
                    onChange={handleChange}
                >
                    <MenuItem value="NEWS_BLOCK">News Block</MenuItem>
                    <MenuItem value="WIDGET_BLOCK">Widget/Ad Block</MenuItem>
                </Select>
            </FormControl>

            {form.blockType === 'NEWS_BLOCK' ? (
                <>
                    <FormControl fullWidth sx={{ mb: 2 }}>
                        <InputLabel>Taxonomy Terms</InputLabel>
                        <Select
                            name="taxonomyTermIds"
                            multiple
                            value={form.taxonomyTermIds}
                            onChange={handleTermChange}
                            renderValue={(selected) => selected.map(id => allTerms.find(t => t.id === id)?.name).join(', ')}
                        >
                            {allTerms.map((term) => (
                                <MenuItem key={term.id} value={term.id}>
                                    <Checkbox checked={form.taxonomyTermIds.indexOf(term.id) > -1} />
                                    {term.name}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                    <TextField
                        name="newsCount"
                        label="Number of News to Display"
                        type="number"
                        value={form.newsCount}
                        onChange={handleChange}
                        fullWidth
                        sx={{ mb: 2 }}
                    />
                    <FormGroup sx={{ mb: 2 }}>
                        <FormControlLabel
                            control={<Checkbox name="showTeaser" checked={form.showTeaser} onChange={handleChange} />}
                            label="Show Teaser"
                        />
                    </FormGroup>
                    <TextField
                        name="titleFontSize"
                        label="Title Font Size (e.g., '1.2rem')"
                        value={form.titleFontSize}
                        onChange={handleChange}
                        fullWidth
                        sx={{ mb: 2 }}
                    />
                </>
            ) : (
                <TextField
                    name="content"
                    label="HTML/JS Content"
                    multiline
                    rows={10}
                    value={form.content}
                    onChange={handleChange}
                    fullWidth
                    sx={{ mb: 2 }}
                />
            )}

            <Box sx={{ mt: 2 }}>
                <Button type="submit" variant="contained" disabled={loading}>
                    {loading ? <CircularProgress size={24} /> : (isNew ? 'Create' : 'Update')}
                </Button>
                <Button onClick={() => router.back()} sx={{ ml: 2 }}>
                    Cancel
                </Button>
            </Box>
        </Box>
    );
};

export default HomePageBlockForm;
