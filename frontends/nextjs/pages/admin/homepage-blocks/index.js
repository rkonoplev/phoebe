import React, { useState, useEffect } from 'react';
import {
    Typography, Container, Button, Box, CircularProgress, Alert,
    Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, IconButton
} from '@mui/material';
import Link from 'next/link';
import { Edit, Delete } from '@mui/icons-material';
import AdminRoute from '../../../components/AdminRoute';
import api from '../../../services/api';

const HomepageBlocksPage = () => {
    const [blocks, setBlocks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const fetchBlocks = async () => {
        try {
            setLoading(true);
            const response = await api.get('/admin/homepage-blocks');
            // Sort by weight to ensure consistent order
            const sortedBlocks = response.data.sort((a, b) => a.weight - b.weight);
            setBlocks(sortedBlocks);
            setError(null);
        } catch (err) {
            setError('Failed to fetch homepage blocks. Please try again.');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchBlocks();
    }, []);

    const handleDelete = async (id) => {
        if (window.confirm('Are you sure you want to delete this block?')) {
            try {
                await api.delete(`/admin/homepage-blocks/${id}`);
                fetchBlocks(); // Refresh the list
            } catch (err) {
                setError('Failed to delete the block.');
                console.error(err);
            }
        }
    };

    if (loading) {
        return <CircularProgress sx={{ display: 'block', margin: 'auto', mt: 4 }} />;
    }

    if (error) {
        return <Alert severity="error" sx={{ mt: 4 }}>{error}</Alert>;
    }

    return (
        <Container>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', my: 4 }}>
                <Typography variant="h4">
                    Homepage Blocks Management
                </Typography>
                <Link href="/admin/homepage-blocks/new" passHref>
                    <Button variant="contained">Create New Block</Button>
                </Link>
            </Box>

            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Weight</TableCell>
                            <TableCell>Type</TableCell>
                            <TableCell>Summary</TableCell>
                            <TableCell align="right">Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {blocks.map((block) => (
                            <TableRow key={block.id}>
                                <TableCell>{block.weight}</TableCell>
                                <TableCell>{block.blockType}</TableCell>
                                <TableCell>
                                    {block.blockType === 'NEWS_BLOCK'
                                        ? `Shows ${block.newsCount} news from ${block.taxonomyTermIds?.length || 0} terms`
                                        : `Widget with custom content`}
                                </TableCell>
                                <TableCell align="right">
                                    <Link href={`/admin/homepage-blocks/edit/${block.id}`} passHref>
                                        <IconButton size="small">
                                            <Edit />
                                        </IconButton>
                                    </Link>
                                    <IconButton size="small" onClick={() => handleDelete(block.id)}>
                                        <Delete />
                                    </IconButton>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </Container>
    );
};

export default () => (
    <AdminRoute>
        <HomepageBlocksPage />
    </AdminRoute>
);
