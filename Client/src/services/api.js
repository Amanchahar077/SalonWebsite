const API_BASE_URL = '/api';

/**
 * Common fetch helper with JSON parsing and credentials support for OAuth sessions
 */
async function fetchApi(endpoint, options = {}) {
  const defaultHeaders = {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  };

  const config = {
    ...options,
    headers: {
      ...defaultHeaders,
      ...options.headers,
    },
    credentials: 'include',
  };

  const response = await fetch(`${API_BASE_URL}${endpoint}`, config);

  if (response.status === 204) {
    return null;
  }

  const data = await response.json().catch(() => ({}));

  if (!response.ok) {
    const error = new Error(data.message || 'An API error occurred');
    error.status = response.status;
    error.data = data;
    throw error;
  }

  return data;
}

export const api = {
  // Auth API
  getAuthMe: () => fetchApi('/auth/me'),
  loginWithGoogle: () => {
    window.location.href = '/oauth2/authorization/google';
  },
  logout: () => fetchApi('/auth/logout', { method: 'POST' }),

  // Salon Public Info
  getSalonConfig: () => fetchApi('/salon/configuration'),

  // Appointments API
  getAvailability: (dateStr) => fetchApi(`/appointments/availability?date=${encodeURIComponent(dateStr)}`),
  bookAppointment: (data) => fetchApi('/appointments', {
    method: 'POST',
    body: JSON.stringify(data),
  }),
  getMyAppointments: () => fetchApi('/appointments/my'),
  getAppointmentDetails: (id) => fetchApi(`/appointments/${id}`),
  cancelAppointment: (id, reason) => fetchApi(`/appointments/${id}/cancel?reason=${encodeURIComponent(reason || '')}`, {
    method: 'PUT',
  }),

  // Payments API
  createPaymentOrder: (appointmentId) => fetchApi(`/payments/create-order?appointmentId=${appointmentId}`, {
    method: 'POST',
  }),
  verifyPayment: (data) => fetchApi('/payments/verify', {
    method: 'POST',
    body: JSON.stringify(data),
  }),

  // Admin APIs
  getAdminDashboard: () => fetchApi('/admin/dashboard'),
  getAdminUsers: () => fetchApi('/admin/users'),
  getAdminAppointments: ({ date, status, providerId, page = 0, size = 20 } = {}) => {
    const params = new URLSearchParams({ page, size });
    if (date) params.append('date', date);
    if (status) params.append('status', status);
    if (providerId) params.append('providerId', providerId);
    return fetchApi(`/admin/appointments?${params.toString()}`);
  },
  getAdminPayments: () => fetchApi('/admin/payments'),
  getAdminProviders: async () => {
    try {
      return await fetchApi('/admin/providers');
    } catch (e) {
      return await fetchApi('/salon/providers');
    }
  },
  updateSalonConfig: (data) => fetchApi('/admin/salon/configuration', {
    method: 'PUT',
    body: JSON.stringify(data),
  }),
  addProvider: (data) => fetchApi('/admin/providers', {
    method: 'POST',
    body: JSON.stringify(data),
  }),
  updateProvider: (id, data) => fetchApi(`/admin/providers/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  }),
  deleteProvider: (id) => fetchApi(`/admin/providers/${id}`, {
    method: 'DELETE',
  }),
  updateProviderStatus: (id, status) => fetchApi(`/admin/providers/${id}/status?status=${status}`, {
    method: 'PUT',
  }),
  getAppointmentHistory: (id) => fetchApi(`/admin/appointments/${id}/history`),
  getNotificationLogs: () => fetchApi('/admin/notifications'),
};
