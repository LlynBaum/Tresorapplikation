import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {postSecret} from "../../comunication/FetchSecrets";

/**
 * NewCredential
 * @author Peter Rutschmann
 */
const NewCredential = () => {
    const initialState = {
        kindid: 1,
        kind:"credential",
        userName: "",
        password: "",
        url: ""
    };
    const [credentialValues, setCredentialValues] = useState(initialState);
    const [errorMessage, setErrorMessage] = useState('');

    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setErrorMessage('');
        try {
            const content = credentialValues;
            await postSecret({content});
            setCredentialValues(initialState);
            navigate('/secret/secrets');
        } catch (error) {
            console.error('Failed to fetch to server:', error.message);
            setErrorMessage(error.message);
        }
    };

    return (
        <div className="card">
            <h1 className="text-center">Add New Credential Secret</h1>
            
            {errorMessage && <div className="error-message">{errorMessage}</div>}
            
            <form onSubmit={handleSubmit} className="card">
                <div className="form-group">
                    <label htmlFor="username">Username:</label>
                    <input
                        id="username"
                        type="text"
                        value={credentialValues.userName}
                        onChange={(e) =>
                            setCredentialValues(prevValues => ({...prevValues, userName: e.target.value}))}
                        required
                        placeholder="Please enter username"
                    />
                </div>
                
                <div className="form-group">
                    <label htmlFor="password">Password:</label>
                    <input
                        id="password"
                        type="password"
                        value={credentialValues.password}
                        onChange={(e) =>
                            setCredentialValues(prevValues => ({...prevValues, password: e.target.value}))}
                        required
                        placeholder="Please enter password"
                    />
                </div>
                
                <div className="form-group">
                    <label htmlFor="url">URL:</label>
                    <input
                        id="url"
                        type="text"
                        value={credentialValues.url}
                        onChange={(e) =>
                            setCredentialValues(prevValues => ({...prevValues, url: e.target.value}))}
                        required
                        placeholder="Please enter url"
                    />
                </div>
                
                <div className="text-center mt-3">
                    <button type="submit">Save Secret</button>
                </div>
            </form>
        </div>
    );
};

export default NewCredential;
