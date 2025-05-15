import { useEffect, useState } from 'react';

function TestConnection() {
  const [response, setResponse] = useState('');

  useEffect(() => {
    fetch('/api/test') // Assumes backend has GET /api/test
      .then((res) => res.text())
      .then((data) => setResponse(data))
      .catch((err) => setResponse('Error connecting to backend'));
  }, []);

  return <div>Backend says: {response}</div>;
}

export default TestConnection;
