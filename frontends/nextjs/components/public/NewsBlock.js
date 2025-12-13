import React from 'react';
import { Typography, Grid, Card, CardContent, CardActionArea } from '@mui/material';
import Link from 'next/link';

const NewsBlock = ({ block }) => {
    if (!block.news || block.news.length === 0) {
        return null; // Don't render anything if there are no news
    }

    return (
        <Grid container spacing={2}>
            {block.news.map(article => (
                <Grid item xs={12} sm={6} md={4} key={article.id}>
                    <Link href={`/node/${article.id}`} passHref legacyBehavior>
                        <CardActionArea component="a" sx={{ height: '100%' }}>
                            <Card>
                                <CardContent>
                                    <Typography
                                        gutterBottom
                                        variant="h6"
                                        component="div"
                                        sx={{ fontSize: block.titleFontSize || '1rem' }}
                                    >
                                        {article.title}
                                    </Typography>
                                    {block.showTeaser && (
                                        <Typography
                                            variant="body2"
                                            color="text.secondary"
                                            dangerouslySetInnerHTML={{ __html: article.teaser }}
                                        />
                                    )}
                                </CardContent>
                            </Card>
                        </CardActionArea>
                    </Link>
                </Grid>
            ))}
        </Grid>
    );
};

export default NewsBlock;
