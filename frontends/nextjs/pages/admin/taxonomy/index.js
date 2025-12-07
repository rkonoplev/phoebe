import React, { useEffect, useState } from 'react';
import AdminRoute from '../../../components/AdminRoute';
import { admin } from '../../../services/api';
import { 
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, 
  Button, IconButton, Typography, Box 
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import Link from 'next/link';

const TaxonomyListPage = () => {
  const [terms, setTerms] = useState([]);

  const fetchTerms = async () => {
    try {
      const response = await admin.getTerms();
      setTerms(response.data);
    } catch (error) {
      console.error('Failed to fetch terms:', error);
    }
  };

  useEffect(() => {
    fetchTerms();
  }, []);

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this term?')) {
      try {
        await admin.deleteTerm(id);
        fetchTerms(); // Refresh the list
      } catch (error) {
        console.error('Failed to delete term:', error);
      }
    }
  };

  return (
    <Box>
      <Typography variant="h4" gutterBottom>Taxonomy Management</Typography>
      <Link href="/admin/taxonomy/new" passHref>
        <Button variant="contained" color="primary" sx={{ mb: 2 }}>
          Create Term
        </Button>
      </Link>
      <Paper>
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>Name</TableCell>
                <TableCell>Vocabulary</TableCell>
                <TableCell align="right">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {terms.map((term) => (
                <TableRow key={term.id}>
                  <TableCell>{term.id}</TableCell>
                  <TableCell>{term.name}</TableCell>
                  <TableCell>{term.vocabulary}</TableCell>
                  <TableCell align="right">
                    <Link href={`/admin/taxonomy/edit/${term.id}`} passHref>
                      <IconButton color="primary"><EditIcon /></IconButton>
                    </Link>
                    <IconButton color="error" onClick={() => handleDelete(term.id)}>
                      <DeleteIcon />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Paper>
    </Box>
  );
};

const ProtectedTaxonomyListPage = () => (
  <AdminRoute>
    <TaxonomyListPage />
  </AdminRoute>
);

export default ProtectedTaxonomyListPage;
