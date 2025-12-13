import React from 'react';
import api from '../services/api';
import { Container, Typography, Grid, Card, CardContent, CardActionArea } from '@mui/material';
import Link from 'next/link';
import NewsBlock from '../components/public/NewsBlock';
import WidgetBlock from '../components/public/WidgetBlock';

const SimpleNewsList = ({ news }) => (
    <Grid container spacing={3}>
        {news.map((article) => (
            <Grid item xs={12} sm={6} md={4} key={article.id}>
                <Link href={`/node/${article.id}`} passHref legacyBehavior>
                    <CardActionArea component="a" sx={{ height: '100%' }}>
                        <Card>
                            <CardContent>
                                <Typography gutterBottom variant="h5" component="div">
                                    {article.title}
                                </Typography>
                                <Typography
                                    variant="body2"
                                    color="text.secondary"
                                    dangerouslySetInnerHTML={{ __html: article.teaser }}
                                />
                            </CardContent>
                        </Card>
                    </CardActionArea>
                </Link>
            </Grid>
        ))}
    </Grid>
);

const CustomBlockList = ({ blocks }) => (
    <Grid container spacing={3}>
        {blocks.map(block => (
            <Grid item xs={12} key={block.id}>
                {block.blockType === 'NEWS_BLOCK' && <NewsBlock block={block} />}
                {block.blockType === 'WIDGET_BLOCK' && <WidgetBlock block={block} />}
            </Grid>
        ))}
    </Grid>
);


const HomePage = ({ homepageData }) => {
    const { mode, news, blocks } = homepageData;

    return (
        <Container>
            <Typography variant="h2" component="h1" gutterBottom>
                {mode === 'SIMPLE' ? 'Latest News' : 'Homepage'}
            </Typography>
            {mode === 'SIMPLE' ? <SimpleNewsList news={news} /> : <CustomBlockList blocks={blocks} />}
        </Container>
    );
};

export async function getServerSideProps() {
    try {
        const response = await api.get('/public/homepage');
        return {
            props: { homepageData: response.data },
        };
    } catch (error) {
        console.error('Failed to fetch homepage data:', error);
        // Fallback to a default simple layout in case of error
        return {
            props: {
                homepageData: {
                    mode: 'SIMPLE',
                    news: [],
                    blocks: []
                }
            },
        };
    }
}

export default HomePage;
