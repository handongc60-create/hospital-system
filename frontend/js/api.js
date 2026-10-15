// API请求封装
const API_BASE = 'http://localhost:8080/api';

const axiosInstance = axios.create({
    baseURL: API_BASE,
    timeout: 10000
});

// 请求拦截器：添加Token
axiosInstance.interceptors.request.use(config => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers['Authorization'] = 'Bearer ' + token;
    }
    return config;
}, error => {
    return Promise.reject(error);
});

// 响应拦截器：处理401
axiosInstance.interceptors.response.use(response => {
    return response.data;
}, error => {
    if (error.response && error.response.status === 401) {
        localStorage.removeItem('token');
        localStorage.removeItem('userInfo');
        window.location.href = 'login.html';
    }
    return Promise.reject(error);
});

// API方法
const api = {
    // 原始axios实例（用于自定义请求）
    request: axiosInstance,

    // 用户相关
    login(username, password) {
        return axiosInstance.post('/user/login', { username, password });
    },
    logout() {
        return axiosInstance.post('/user/logout');
    },
    getUserInfo() {
        return axiosInstance.get('/user/info');
    },
    changePassword(oldPassword, newPassword) {
        return axiosInstance.post('/user/change-password', { oldPassword, newPassword });
    },

    // 科室相关
    getDepartmentList() {
        return axiosInstance.get('/department/list');
    },
    getDepartmentDetail(deptId) {
        return axiosInstance.get('/department/' + deptId);
    },
    getDepartmentDoctors(deptId) {
        return axiosInstance.get('/department/' + deptId + '/doctors');
    },

    // 挂号相关
    makeAppointment(data) {
        return axiosInstance.post('/appointment/make', data);
    },
    cancelAppointment(apptId, reason) {
        return axiosInstance.post('/appointment/cancel/' + apptId, null, { params: { reason } });
    },
    getAvailableSchedules(deptId, doctorId, startDate, endDate) {
        return axiosInstance.get('/appointment/schedules', {
            params: { deptId, doctorId, startDate, endDate }
        });
    },
    getMyAppointments(page, size, status) {
        return axiosInstance.get('/appointment/my', { params: { page, size, status } });
    },
    getAppointmentDetail(apptId) {
        return axiosInstance.get('/appointment/' + apptId);
    },
    callNextPatient(apptId) {
        return axiosInstance.post('/appointment/call', null, { params: { apptId } });
    },
    finishVisit(apptId) {
        return axiosInstance.post('/appointment/finish/' + apptId);
    },
    getDoctorQueue(page, size, visitDate, visitStatus) {
        return axiosInstance.get('/appointment/doctor/queue', {
            params: { page, size, visitDate, visitStatus }
        });
    },

    // 处方相关
    createPrescription(data) {
        return axiosInstance.post('/prescription/create', data);
    },
    getPrescriptionDetail(prescId) {
        return axiosInstance.get('/prescription/' + prescId);
    },
    getPrescriptionItems(prescId) {
        return axiosInstance.get('/prescription/' + prescId + '/items');
    },
    getMyPrescriptions(page, size) {
        return axiosInstance.get('/prescription/my', { params: { page, size } });
    },
    cancelPrescription(prescId) {
        return axiosInstance.post('/prescription/cancel/' + prescId);
    },

    // 支付相关
    createPayment(data) {
        return axiosInstance.post('/payment/create', data);
    },
    paymentCallback(data) {
        return axiosInstance.post('/payment/callback', data);
    },
    refundPayment(payId) {
        return axiosInstance.post('/payment/refund/' + payId);
    },
    getPaymentDetail(payId) {
        return axiosInstance.get('/payment/' + payId);
    },
    getMyPayments(page, size) {
        return axiosInstance.get('/payment/my', { params: { page, size } });
    },
    onsitePayment(data) {
        return axiosInstance.post('/payment/onsite', data);
    },
    getCashierPayments(page, size, apptId) {
        return axiosInstance.get('/payment/cashier/list', {
            params: { page, size, apptId }
        });
    }
};
