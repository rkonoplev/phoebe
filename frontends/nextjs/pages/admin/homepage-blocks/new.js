import React from 'react';
import { Container } from '@mui/material';
import AdminRoute from '../../../components/AdminRoute';
import HomePageBlockForm from '../../../components/admin/homepage/HomePageBlockForm';

const NewHomePageBlockPage = () => {
    return (
        <Container>
            <HomePageBlockForm />
        </Container>
    );
};

export default () => (
    <AdminRoute>
        <NewHomePageBlockPage />
    </AdminRoute>
);
