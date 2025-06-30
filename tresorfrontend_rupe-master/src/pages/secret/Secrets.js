import '../../App.css';
import React, {useEffect, useState} from 'react';
import {getSecretsforUser} from "../../comunication/FetchSecrets";
import { useLocation } from 'react-router-dom';

/**
 * Secrets
 * @author Peter Rutschmann
 */
const Secrets = () => {
    const [secrets, setSecrets] = useState([]);
    const [errorMessage, setErrorMessage] = useState('');
    const location = useLocation();

    useEffect(() => {
        const fetchSecrets = async () => {
            setErrorMessage('');
            try {
                const data = await getSecretsforUser();
                console.log(data);
                setSecrets(data);
            } catch (error) {
                console.error('Failed to fetch to server:', error.message);
                setErrorMessage(error.message);
            }
        };
        fetchSecrets();
    }, [location]);

    return (
        <div className="card">
            <h1 className="text-center">My Secrets</h1>
            {errorMessage && <div className="error-message">{errorMessage}</div>}
            
            <div className="card">
                <h2>Secrets</h2>
                <table>
                    <thead>
                    <tr>
                        <th>Secret ID</th>
                        <th>User ID</th>
                        <th>Content</th>
                    </tr>
                    </thead>
                    <tbody>
                    {secrets?.length > 0 ? (
                        secrets.map(secret => (
                            <tr key={secret.id}>
                                <td>{secret.id}</td>
                                <td>{secret.userId}</td>
                                <td>
                                    <pre style={{whiteSpace: 'pre-wrap', backgroundColor: 'var(--background)', padding: 'var(--spacing-sm)', borderRadius: 'var(--border-radius-sm)'}}>
                                        {JSON.stringify(secret.content, null, 2)}
                                    </pre>
                                </td>
                            </tr>
                        ))
                    ) : (
                        <tr>
                            <td colSpan="3" className="text-center">No secrets available</td>
                        </tr>
                    )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default Secrets;