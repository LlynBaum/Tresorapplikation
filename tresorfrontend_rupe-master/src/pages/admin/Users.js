import '../../App.css';
import React, {useEffect, useState} from "react";
import {getUsers} from "../../comunication/FetchUser";
import { useLocation } from 'react-router-dom';

/**
 * Users
 * @author Peter Rutschmann
 */
const Users = () => {
    const [users, setUsers] = useState([]);
    const [errorMessage, setErrorMessage] = useState('');
    const location = useLocation();

    useEffect(() => {
        const fetchUsers = async () => {
            try {
                const users = await getUsers();
                console.log(users);
                setUsers(users);
            } catch (error) {
                console.error('Failed to fetch to server:', error.message);
                setErrorMessage(error.message);
            }
        };
        fetchUsers();
    }, [location]);

    return (
        <>
            <h1>Client list</h1>
            <ul>
                {users.map(user => (
                    <li key={user.id}>{user.id} {user.firstName} {user.lastName} - {user.email} - {user.password}</li>
                ))}
            </ul>
            {errorMessage && <p style={{ color: 'red' }}>{errorMessage}</p>}
        </>
    );
};

export default Users;