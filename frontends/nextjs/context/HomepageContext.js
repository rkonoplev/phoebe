import React, { createContext, useState, useContext, useEffect } from 'react';
import api from '../services/api';

const HomepageContext = createContext();

export const HomepageProvider = ({ children }) => {
    const [mode, setMode] = useState('SIMPLE');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchMode = async () => {
            try {
                const response = await api.get('/public/homepage/mode');
                setMode(response.data.mode);
            } catch (error) {
                console.error('Failed to fetch homepage mode:', error);
            } finally {
                setLoading(false);
            }
        };
        fetchMode();
    }, []);

    const toggleMode = async () => {
        const newMode = mode === 'SIMPLE' ? 'CUSTOM' : 'SIMPLE';
        try {
            await api.patch('/public/homepage/mode', { mode: newMode });
            setMode(newMode);
            // Reload the page to reflect the new layout structure
            window.location.reload();
        } catch (error) {
            console.error('Failed to toggle homepage mode:', error);
        }
    };

    return (
        <HomepageContext.Provider value={{ mode, toggleMode, loading }}>
            {children}
        </HomepageContext.Provider>
    );
};

export const useHomepage = () => useContext(HomepageContext);
