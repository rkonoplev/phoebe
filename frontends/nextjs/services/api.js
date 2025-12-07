import axios from 'axios';

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// --- Public API ---

export const getNews = (page = 0, size = 10) => {
  return api.get(`/public/news?page=${page}&size=${size}`);
};

export const getNewsById = (id) => {
  return api.get(`/public/news/${id}`);
};

export const getNewsByTerm = (termId, page = 0, size = 10) => {
  return api.get(`/public/news/term/${termId}?page=${page}&size=${size}`);
};

export const searchPublicNews = (query) => {
  return api.get(`/public/news/search?q=${encodeURIComponent(query)}`);
};


// --- Admin API ---

// Request interceptor to add auth token to every admin request
api.interceptors.request.use(config => {
    if (typeof window !== 'undefined') {
        const token = localStorage.getItem('authToken');
        // Check if the URL is an admin URL
        if (token && config.url.startsWith('/admin')) {
            config.headers.Authorization = `Basic ${token}`;
        }
    }
    return config;
});

// Response interceptor to handle auth errors globally
api.interceptors.response.use(
    response => response,
    error => {
        if (typeof window !== 'undefined' && error.response) {
            if (error.response.status === 401 || error.response.status === 403) {
                // Clear auth data
                localStorage.removeItem('authToken');
                // Redirect to login if on admin page
                if (window.location.pathname.startsWith('/admin') && 
                    window.location.pathname !== '/admin/login') {
                    window.location.href = '/admin/login';
                }
            }
        }
        return Promise.reject(error);
    }
);


export const admin = {
  // Auth
  getMe: () => api.get('/admin/auth/me'), // Get current user info

  // News
  getNews: (page = 0, size = 20) => api.get(`/admin/news?page=${page}&size=${size}`),
  getNewsById: (id) => api.get(`/admin/news/${id}`),
  createNews: (data) => api.post('/admin/news', data),
  updateNews: (id, data) => api.put(`/admin/news/${id}`, data),
  deleteNews: (id) => api.delete(`/admin/news/${id}`),

  // Taxonomy
  getTerms: () => api.get('/admin/terms'),
  getTermById: (id) => api.get(`/admin/terms/${id}`),
  createTerm: (data) => api.post('/admin/terms', data),
  updateTerm: (id, data) => api.put(`/admin/terms/${id}`, data),
  deleteTerm: (id) => api.delete(`/admin/terms/${id}`),

  // Users
  getUsers: () => api.get('/admin/users'),
  getUserById: (id) => api.get(`/admin/users/${id}`),
  createUser: (data) => api.post('/admin/users', data),
  updateUser: (id, data) => api.put(`/admin/users/${id}`, data),
  deleteUser: (id) => api.delete(`/admin/users/${id}`),

  // Roles
  getRoles: () => api.get('/admin/roles'),
};


export default api;
