module.exports = function(grunt) {

  grunt.initConfig({});

  grunt.registerTask('default', 'Create a test file', function() {
    grunt.file.write('test.txt', '');
  });

};
