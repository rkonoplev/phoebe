import React from 'react';
import { Container, Typography, Box, Paper } from '@mui/material';
import Head from 'next/head';
import axios from 'axios';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080/api';

export default function StaticPage({ page, error }) {
  if (error) {
    return (
      <Container maxWidth="md" sx={{ py: 4 }}>
        <Typography variant="h4" color="error">Page Not Found</Typography>
        <Typography>{error}</Typography>
      </Container>
    );
  }

  if (!page) {
    return (
      <Container maxWidth="md" sx={{ py: 4 }}>
        <Typography variant="h4">Loading...</Typography>
      </Container>
    );
  }

  return (
    <>
      <Head>
        <title>{page.title} - Phoebe News</title>
        <meta name="description" content={page.teaser || page.title} />
      </Head>
      <Container maxWidth="md" sx={{ py: 4 }}>
        <Paper elevation={0} sx={{ p: 4 }}>
          <Typography variant="h3" component="h1" gutterBottom sx={{ fontFamily: 'Roboto Slab, serif' }}>
            {page.title}
          </Typography>
          <Box 
            sx={{ mt: 3 }}
            dangerouslySetInnerHTML={{ __html: page.body }}
          />
        </Paper>
      </Container>
    </>
  );
}

export async function getStaticPaths() {
  const slugs = ['about', 'contact', 'privacy', 'terms-of-service', 'advertising', 
                 'editorial-statute', 'community-rules', 'technical-sheet'];
  
  return {
    paths: slugs.map(slug => ({ params: { slug } })),
    fallback: 'blocking'
  };
}

export async function getStaticProps({ params }) {
  const { slug } = params;
  const termName = slug.replace(/-/g, '_');

  try {
    const termsResponse = await axios.get(`${API_BASE_URL}/public/terms`);
    const terms = termsResponse.data;
    
    const pageTerm = terms.find(t => t.vocabulary === 'page_type' && t.name === termName);
    
    if (!pageTerm) {
      return { notFound: true };
    }

    const pageResponse = await axios.get(`${API_BASE_URL}/public/news/term/${pageTerm.id}`);
    const pages = pageResponse.data.content || pageResponse.data;
    
    if (!pages || pages.length === 0) {
      return { notFound: true };
    }

    return {
      props: { page: pages[0] },
      revalidate: 3600
    };
  } catch (error) {
    console.error('Error fetching page:', error);
    return { notFound: true };
  }
}
