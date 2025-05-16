import '../App.css';

/**
 * Home
 * @author Peter Rutschmann
 */
const Home = () => {
    return (
        <>
            <div className="card">
                <h1 className="text-center">Speichern Sie Ihre Daten sicher ab.</h1>
                <div className="card">
                    <p>In dieser Applikation können Sie, nachdem Sie sich registriert haben, Ihre sensitiven Daten verschlüsselt
                        in einer Datenbank speichern.</p>
                    <p>Erstellen Sie ein neues Secret. Wählen Sie zwischen Credentials, Credit-Cards und Notes.</p>
                </div>
            </div>
        </>
    );
};

export default Home;