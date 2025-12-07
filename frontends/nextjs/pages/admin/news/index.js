import React, { useEffect, useState } from 'react';
import ProtectedRoute from '../../../components/ProtectedRoute';
import { admin } from '../../../services/api';
import { 
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, 
  Button, IconButton, Typography, Box, TablePagination, Checkbox, Chip, Alert
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import Link from 'next/link';

const NewsListPage = () => {
  const [news, setNews] = useState([]);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(20);
  const [totalElements, setTotalElements] = useState(0);
  const [selected, setSelected] = useState([]);
  const [successMessage, setSuccessMessage] = useState('');

  const fetchNews = async () => {
    try {
      const response = await admin.getNews(page, rowsPerPage);
      setNews(response.data.content);
      setTotalElements(response.data.totalElements);
    } catch (error) {
      console.error('Failed to fetch news:', error);
    }
  };

  useEffect(() => {
    fetchNews();
  }, [page, rowsPerPage]);

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this article?')) {
      try {
        await admin.deleteNews(id);
        fetchNews(); // Refresh the list
      } catch (error) {
        console.error('Failed to delete news:', error);
      }
    }
  };

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
    setSelected([]);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
    setSelected([]);
  };

  const handleSelectAll = (event) => {
    if (event.target.checked) {
      setSelected(news.map(n => n.id));
    } else {
      setSelected([]);
    }
  };

  const handleSelect = (id) => {
    setSelected(prev => 
      prev.includes(id) ? prev.filter(i => i !== id) : [...prev, id]
    );
  };

  const handleBulkDelete = async () => {
    if (!confirm(`Delete ${selected.length} selected articles?`)) return;
    try {
      const response = await admin.bulkAction({
        action: 'DELETE',
        filterType: 'BY_IDS',
        itemIds: selected,
        confirmed: true
      });
      setSuccessMessage(`Deleted ${response.data.affectedCount} articles`);
      setSelected([]);
      fetchNews();
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (error) {
      console.error('Bulk delete failed:', error);
      alert('Failed to delete articles');
    }
  };

  const handleBulkUnpublish = async () => {
    if (!confirm(`Unpublish ${selected.length} selected articles?`)) return;
    try {
      const response = await admin.bulkAction({
        action: 'UNPUBLISH',
        filterType: 'BY_IDS',
        itemIds: selected,
        confirmed: true
      });
      setSuccessMessage(`Unpublished ${response.data.affectedCount} articles`);
      setSelected([]);
      fetchNews();
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (error) {
      console.error('Bulk unpublish failed:', error);
      alert('Failed to unpublish articles');
    }
  };

  return (
    <Box>
      <Typography variant="h4" gutterBottom>News Management</Typography>
      {successMessage && <Alert severity="success" sx={{ mb: 2 }}>{successMessage}</Alert>}
      <Box sx={{ mb: 2, display: 'flex', gap: 2 }}>
        <Link href="/admin/news/new" passHref>
          <Button variant="contained" color="primary">
            Create News
          </Button>
        </Link>
        {selected.length > 0 && (
          <>
            <Button variant="outlined" color="error" onClick={handleBulkDelete}>
              Delete Selected ({selected.length})
            </Button>
            <Button variant="outlined" color="warning" onClick={handleBulkUnpublish}>
              Unpublish Selected ({selected.length})
            </Button>
          </>
        )}
      </Box>
      <Paper>
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell padding="checkbox">
                  <Checkbox
                    checked={selected.length === news.length && news.length > 0}
                    indeterminate={selected.length > 0 && selected.length < news.length}
                    onChange={handleSelectAll}
                  />
                </TableCell>
                <TableCell>Title</TableCell>
                <TableCell>Status</TableCell>
                <TableCell>Author</TableCell>
                <TableCell>Last Modified</TableCell>
                <TableCell align="right">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {news.map((article) => (
                <TableRow key={article.id} selected={selected.includes(article.id)}>
                  <TableCell padding="checkbox">
                    <Checkbox
                      checked={selected.includes(article.id)}
                      onChange={() => handleSelect(article.id)}
                    />
                  </TableCell>
                  <TableCell>{article.title}</TableCell>
                  <TableCell>
                    <Chip 
                      label={article.published ? 'Published' : 'Draft'} 
                      color={article.published ? 'success' : 'default'}
                      size="small"
                    />
                  </TableCell>
                  <TableCell>{article.author?.username || 'Unknown'}</TableCell>
                  <TableCell>{new Date(article.lastModifiedDate).toLocaleString()}</TableCell>
                  <TableCell align="right">
                    <Link href={`/admin/news/edit/${article.id}`} passHref>
                      <IconButton color="primary"><EditIcon /></IconButton>
                    </Link>
                    <IconButton color="error" onClick={() => handleDelete(article.id)}>
                      <DeleteIcon />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
        <TablePagination
          component="div"
          count={totalElements}
          page={page}
          onPageChange={handleChangePage}
          rowsPerPage={rowsPerPage}
          onRowsPerPageChange={handleChangeRowsPerPage}
          rowsPerPageOptions={[20, 50, 100]}
        />
      </Paper>
    </Box>
  );
};

const ProtectedNewsListPage = () => (
  <ProtectedRoute>
    <NewsListPage />
  </ProtectedRoute>
);

export default ProtectedNewsListPage;
