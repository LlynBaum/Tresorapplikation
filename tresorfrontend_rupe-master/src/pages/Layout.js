import { Outlet, Link } from "react-router-dom";

/**
 * Layout
 * @author Peter Rutschmann
 */
const Layout = ({loginValues}) => {
    return (
        <>
            <nav>
                <div className="nav-header">
                    <h1>The secret tresor application</h1>
                    <div className={`user-status ${loginValues.email ? 'logged-in' : ''}`}>
                        {loginValues.email === '' ? 'No user logged in' : 'User: ' + loginValues.email}
                    </div>
                </div>
                <ul>
                    <li>
                        <a href="/">Secrets</a>
                        <ul>
                            <li><Link to="/secret/secrets">My secrets</Link></li>
                            <li><Link to="/secret/newcredential">New credential</Link></li>
                            <li><Link to="/secret/newcreditcard">New credit-card</Link></li>
                            <li><Link to="/secret/newnote">New note</Link></li>
                        </ul>
                    </li>
                    <li>
                        <a href="/">User</a>
                        <ul>
                            <li><Link to="/user/login">Login</Link></li>
                            <li><Link to="/user/register">Register</Link></li>
                        </ul>
                    </li>
                    <li>
                        <a href="/">Admin</a>
                        <ul>
                            <li><Link to="/user/users">All users</Link></li>
                            <li>Add user</li>
                            <li><Link to="/user/users/:id">Edit user</Link></li>
                            <li>All secrets</li>
                        </ul>
                    </li>
                    <li>
                        <Link to="/">About</Link>
                    </li>
                </ul>
            </nav>
            <div className="container">
                <main>
                    <Outlet />
                </main>
            </div>
        </>
    )
};

export default Layout;