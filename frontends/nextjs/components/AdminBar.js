import React from 'react';
import { AppBar, Toolbar, Button, Box, Typography, Chip } from '@mui/material';
import Link from 'next/link';
import { useAuth } from '../context/AuthContext';
import { useRouter } from 'next/router';

const AdminBar = () => {
  const { user, logout } = useAuth();
  const router = useRouter();

  const handleLogout = () => {
    logout();
    router.push('/');
  };

  const isAdmin = user?.role?.name === 'ADMIN';
  const roleName = user?.role?.name || 'USER';
  const username = user?.username || 'Unknown';

  return (
    <AppBar position="static" sx={{ backgroundColor: '#000' }}>
      <Toolbar variant="dense">
        <Box sx={{ flexGrow: 1, display: 'flex', gap: 2 }}>
          <Link href="/" passHref>
            <Button sx={{ color: 'white' }}>Home</Button>
          </Link>
          <Link href="/admin/news/new" passHref>
            <Button sx={{ color: 'white' }}>Create News</Button>
          </Link>
          <Link href="/admin/news" passHref>
            <Button sx={{ color: 'white' }}>News List</Button>
          </Link>

          {isAdmin && (
            <>
              <Link href="/admin/taxonomy" passHref>
                <Button sx={{ color: 'white' }}>Taxonomy</Button>
              </Link>
              <Link href="/admin/users" passHref>
                <Button sx={{ color: 'white' }}>Users</Button>
              </Link>
            </>
          )}
        </Box>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          <Typography variant="body2" sx={{ color: 'white' }}>
            {username}
          </Typography>
          <Chip 
            label={roleName} 
            size="small" 
            sx={{ 
              backgroundColor: isAdmin ? '#cc0000' : '#1c355e',
              color: 'white',
              fontWeight: 'bold'
            }} 
          />
          <Button color="inherit" onClick={handleLogout}>
            Logout
          </Button>
        </Box>
      </Toolbar>
    </AppBar>
  );
};

export default AdminBar;
