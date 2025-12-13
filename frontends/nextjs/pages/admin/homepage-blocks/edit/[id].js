import React from 'react';
import { Container } from '@mui/material';
import { useRouter } from 'next/router';
import AdminRoute from '../../../../components/AdminRoute';
import HomePageBlockForm from '../../../../components/admin/homepage/HomePageBlockForm';

const EditHomePageBlockPage = () => {
    const router = useRouter();
    const { id } = router.query;

    // Render the form only when the id is available
    return (
        <Container>
            {id && <HomePageBlockForm blockId={id} />}
        </Container>
    );
};

export default () => (
    <AdminRoute>
        <EditHomePageBlockPage />
    </AdminRoute>
);
