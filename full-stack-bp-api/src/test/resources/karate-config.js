function fn() {
  let port = karate.properties['karate.server.port'] || '8080';
  let config = {
    baseUrl: 'http://localhost:' + port + '/api'
  };
  karate.log('karate baseUrl =', config.baseUrl);
  return config;
}
