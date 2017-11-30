var gulp = require('gulp');

gulp.task('default', function() {
  var fs = require('fs');
  fs.writeFile("test.txt", "Hey there!", function(err) {
    if (err) {
      return console.log(err);
    }

    console.log("The file was saved!");
  });
});
