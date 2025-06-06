import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { postUser } from "../../comunication/FetchUser";
import ReCAPTCHA from "react-google-recaptcha";

/**
 * RegisterUser
 * @author Peter Rutschmann
 */
function RegisterUser({ loginValues, setLoginValues }) {
    const navigate = useNavigate();

    const initialState = {
        firstName: "",
        lastName: "",
        email: "",
        password: "",
        passwordConfirmation: "",
        errorMessage: ""
    };
    const [credentials, setCredentials] = useState(initialState);
    const [errorMessage, setErrorMessage] = useState('');
    const [recaptchaToken, setRecaptchaToken] = useState(null); // State for ReCAPTCHA token

    const validatePassword = (password) => {
        const regex = /^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%/*?&])[A-Za-z\d@/$!%*?&]{8,}$/;
        return regex.test(password);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setErrorMessage('');

        // Validate password confirmation
        if (credentials.password !== credentials.passwordConfirmation) {
            console.log("password != passwordConfirmation");
            setErrorMessage('Password and password-confirmation are not equal.');
            return;
        }

        // Validate password strength
        if (!validatePassword(credentials.password)) {
            console.log("Invalid password format");
            setErrorMessage('Password must contain at least one number, one symbol, one capital letter, and be at least 8 characters long.');
            return;
        }

        // Validate ReCAPTCHA
        if (!recaptchaToken) {
            console.log("ReCAPTCHA not completed");
            setErrorMessage('Please complete the ReCAPTCHA challenge.');
            return;
        }

        try {
            await postUser({ ...credentials, recaptchaToken }); // Include ReCAPTCHA token in the request
            setLoginValues({ userName: credentials.email, password: credentials.password });
            setCredentials(initialState);
            navigate('/');
        } catch (error) {
            console.error('Failed to fetch to server:', error.message);
            setErrorMessage(error.message);
        }
    };

    return (
        <div className="form-container">
            <h2 className="form-title">Register User</h2>
            <form onSubmit={handleSubmit} className="form">
                <section className="form-section">
                    <aside className="form-aside">
                        <div className="form-group">
                            <label>Firstname:</label>
                            <input
                                type="text"
                                className="form-input"
                                value={credentials.firstName}
                                onChange={(e) =>
                                    setCredentials(prevValues => ({ ...prevValues, firstName: e.target.value }))}
                                required
                                placeholder="Please enter your firstname *"
                            />
                        </div>
                        <div className="form-group">
                            <label>Lastname:</label>
                            <input
                                type="text"
                                className="form-input"
                                value={credentials.lastName}
                                onChange={(e) =>
                                    setCredentials(prevValues => ({ ...prevValues, lastName: e.target.value }))}
                                required
                                placeholder="Please enter your lastname *"
                            />
                        </div>
                        <div className="form-group">
                            <label>Email:</label>
                            <input
                                type="text"
                                className="form-input"
                                value={credentials.email}
                                onChange={(e) =>
                                    setCredentials(prevValues => ({ ...prevValues, email: e.target.value }))}
                                required
                                placeholder="Please enter your email"
                            />
                        </div>
                    </aside>
                    <aside className="form-aside">
                        <div className="form-group">
                            <label>Password:</label>
                            <input
                                type="password"
                                className="form-input"
                                value={credentials.password}
                                onChange={(e) =>
                                    setCredentials(prevValues => ({ ...prevValues, password: e.target.value }))}
                                required
                                placeholder="Please enter your password *"
                            />
                        </div>
                        <div className="form-group">
                            <label>Password Confirmation:</label>
                            <input
                                type="password"
                                className="form-input"
                                value={credentials.passwordConfirmation}
                                onChange={(e) =>
                                    setCredentials(prevValues => ({ ...prevValues, passwordConfirmation: e.target.value }))}
                                required
                                placeholder="Please confirm your password *"
                            />
                        </div>
                    </aside>
                </section>
                <div className="form-group">
                    <ReCAPTCHA
                        sitekey={process.env.REACT_APP_CAPCHA_SITE_KEY}
                        onChange={(token) => setRecaptchaToken(token)} // Update token state on completion
                    />
                </div>
                <button type="submit" className="form-button">Register</button>
                {errorMessage && <p className="form-error">{errorMessage}</p>}
            </form>
        </div>
    );
}

export default RegisterUser;
