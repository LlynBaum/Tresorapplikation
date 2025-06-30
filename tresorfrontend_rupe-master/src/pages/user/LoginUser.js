import { useNavigate } from 'react-router-dom';
import { useState } from 'react';
import { loginUser } from '../../comunication/FetchUser'; // Import the login function

/**
 * LoginUser
 * @author Peter Rutschmann
 */
function LoginUser() {
    const navigate = useNavigate();
    const [errorMessage, setErrorMessage] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    const [loginValues, setLoginValues] = useState({email: '', password: ''});

    const handleSubmit = async (e) => {
        e.preventDefault();
        setErrorMessage('');
        setSuccessMessage('');

        try {
            const { ok, status } = await loginUser(); // Destructure the response

            if (ok) {
                setSuccessMessage('Login successful! Redirecting...');
                setTimeout(() => navigate('/'), 2000); // Redirect after 2 seconds
            } else if (status === 401) {
                setErrorMessage('Login failed. Invalid email or password.');
            } else {
                setErrorMessage('An unexpected error occurred.');
            }
        } catch (error) {
            setErrorMessage(error.message || 'An error occurred during login.');
        }
    };

    return (
        <div className="form-container">
            <h2 className="form-title">Login User</h2>
            <form onSubmit={handleSubmit} className="form">
                <section className="form-section">
                    <aside className="form-aside">
                        <div className="form-group">
                            <label>Email:</label>
                            <input
                                type="text"
                                className="form-input"
                                value={loginValues.email}
                                onChange={(e) =>
                                    setLoginValues(prevValues => ({ ...prevValues, email: e.target.value }))}
                                required
                                placeholder="Please enter your email *"
                            />
                        </div>
                        <div className="form-group">
                            <label>Password:</label>
                            <input
                                type="password"
                                className="form-input"
                                value={loginValues.password}
                                onChange={(e) =>
                                    setLoginValues(prevValues => ({ ...prevValues, password: e.target.value }))}
                                required
                                placeholder="Please enter your password *"
                            />
                        </div>
                    </aside>
                </section>
                <button type="submit" className="form-button">Login</button>
                {errorMessage && <p className="form-error">{errorMessage}</p>}
                {successMessage && <p className="form-success">{successMessage}</p>}
            </form>
        </div>
    );
}

export default LoginUser;
