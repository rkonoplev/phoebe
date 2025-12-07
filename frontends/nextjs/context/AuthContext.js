import React, { createContext, useState, useContext, useEffect } from 'react';
import { admin } from '../services/api';
import api from '../services/api'; // Import the base api instance for interceptor setup

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  const fetchUser = async () => {
    try {
      const response = await admin.getMe();
      setUser(response.data);
    } catch (error) {
      console.error("Failed to fetch user, logging out.", error);
      logout();
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const token = localStorage.getItem('authToken');
    if (token) {
      fetchUser();
    } else {
      setLoading(false);
    }
  }, []);

  const login = async (username, password) => {
    try {
      const token = btoa(`${username}:${password}`);
      localStorage.setItem('authToken', token);
      
      const response = await admin.getMe(); // Test login and get user data in one go
      setUser(response.data);
      return true;
    } catch (error) {
      console.error('Login failed:', error);
      logout(); // Clear any partial auth state
      return false;
    }
  };

  const logout = () => {
    localStorage.removeItem('authToken');
    setUser(null);
  };

  const value = {
    user,
    login,
    logout,
    isAuthenticated: !!user,
    loading,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  return useContext(AuthContext);
};
