import React from 'react';
import { Box } from '@mui/material';

const WidgetBlock = ({ block }) => {
    if (!block.content) {
        return null; // Don't render if there is no content
    }

    return (
        <Box dangerouslySetInnerHTML={{ __html: block.content }} />
    );
};

export default WidgetBlock;
