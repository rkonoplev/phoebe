import React, { useState } from 'react';
import { AppBar, Toolbar, Typography, Container, Box, Grid, TextField, IconButton, Button } from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import Head from 'next/head';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useAuth } from '../context/AuthContext';
import { useThemeContext } from '../context/ThemeContext';
import AdminBar from './AdminBar';

const FooterLink = ({ href, children, ...props }) => (
  <Link href={href} passHref>
    <Typography 
      variant="body2" 
      sx={{ color: 'white', textDecoration: 'none', '&:hover': { textDecoration: 'underline' }, cursor: 'pointer' }}
      {...props}
    >
      {children}
    </Typography>
  </Link>
);

const SearchBar = () => {
  const [query, setQuery] = useState('');
  const router = useRouter();

  const handleSearch = (e) => {
    e.preventDefault();
    if (query.trim()) {
      router.push(`/search?q=${encodeURIComponent(query)}`);
    }
  };

  return (
    <Box component="form" onSubmit={handleSearch} sx={{ display: 'flex', alignItems: 'center' }}>
      <TextField
        variant="standard"
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        placeholder="Search…"
        size="small"
        sx={{ 
          color: 'white',
          '& .MuiInputBase-root': { color: 'white' },
          '& .MuiInput-underline:before': { borderBottomColor: 'rgba(255, 255, 255, 0.7)' },
        }}
      />
      <IconButton type="submit" sx={{ p: '10px', color: 'white' }} aria-label="search">
        <SearchIcon />
      </IconButton>
    </Box>
  );
};

const Layout = ({ children }) => {
  const { isAuthenticated } = useAuth();
  const { toggleTheme } = useThemeContext();

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh', bgcolor: 'background.default', color: 'text.primary' }}>
      <Head>
        <title>Phoebe News</title>
        <meta name="viewport" content="initial-scale=1, width=device-width" />
        <link
          rel="stylesheet"
          href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&family=Roboto+Slab:wght@400;700&display=swap"
        />
      </Head>
      
      {isAuthenticated && <AdminBar />}

      <AppBar position="static" sx={{ display: isAuthenticated ? 'none' : 'block' }}>
        <Toolbar>
          <Link href="/" passHref legacyBehavior>
            <Typography variant="h6" component="a" sx={{ flexGrow: 1, color: 'white', textDecoration: 'none' }}>
              Phoebe News
            </Typography>
          </Link>
          <SearchBar />
        </Toolbar>
      </AppBar>

      <Box component="main" sx={{ flexGrow: 1 }}>
        <Container sx={{ py: isAuthenticated ? 2 : 4 }}>
          {children}
        </Container>
      </Box>

      <Box component="footer" sx={{ bgcolor: 'primary.main', color: 'white', p: 4, mt: 'auto' }}>
        <Container maxWidth="lg">
          <Grid container spacing={2} alignItems="center" justifyContent="center" sx={{ textAlign: 'center' }}>
            <Grid item xs={12}>
              <Typography variant="h5" component="div" sx={{ mb: 2 }}>
                PHOEBE
              </Typography>
            </Grid>
            <Grid item xs={12} sx={{ display: 'flex', justifyContent: 'center', flexWrap: 'wrap', gap: '0.5rem 1rem' }}>
              {/* These links will now point to news nodes with a specific taxonomy term */}
              <FooterLink href="/node/1">About Us</FooterLink> {/* Example: node ID 1 for About Us */}
              <Typography>|</Typography>
              <FooterLink href="/node/2">Terms of Service</FooterLink> {/* Example: node ID 2 */}
              <Typography>|</Typography>
              <FooterLink href="/node/3">Privacy Policy</FooterLink> {/* Example: node ID 3 */}
              <Typography>|</Typography>
              <FooterLink href="/node/4">Advertise</FooterLink> {/* Example: node ID 4 */}
              <Typography>|</Typography>
              <Button onClick={toggleTheme} sx={{ color: 'white', p: 0, minWidth: 0, textTransform: 'none' }}>
                <Typography variant="body2" sx={{ textDecoration: 'underline' }}>
                  Toggle Theme
                </Typography>
              </Button>
              <Typography>|</Typography>
              <FooterLink href="/node/5">Contact</FooterLink> {/* Example: node ID 5 */}
            </Grid>
            <Grid item xs={12} sx={{ mt: 2 }}>
              <Typography variant="body2">
                Copyright © Phoebe News {new Date().getFullYear()}. All Rights Reserved.
              </Typography>
            </Grid>
          </Grid>
        </Container>
      </Box>
    </Box>
  );
};

export default Layout;
