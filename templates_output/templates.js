spike.core.Assembler.sourcePath='templates_input';spike.core.Templates.templates['templates_input_watch_watch_partial_html']=function(scope){var t='';t+='<div class=\"loginController\" id=\"element-1\">';t+='<div class=\"box\">';t+='<div class=\"logo\"><\/div>';t+='<form autocomplete=\"off\" sp-watch=\"watcher-0\" spike-unbinded=\"\" spike-event-submit=\"scope.login(event)\" id=\"spike-event-1\">';t+='<input type=\"text\" name=\"loginName\" placeholder=\"'+( spike.core.Message.get('login_placeholder') )+'\">';t+='<input type=\"password\" name=\"password\" placeholder=\"'+( spike.core.Message.get('password_placeholder') )+'\">';t+='<button type=\"submit\" class=\"default\">'+(spike.core.Message.get('login_button'))+'<\/button>';t+='<p class=\"error '+( scope.model.error === true ? 'show' : 'hide' )+'\">'+(spike.core.Message.get('login_error'))+'<\/p>';t+='</form>';var x = 'ok';t+='<div sp-watch=\"watcher-1\" id=\"watcher-1\">';t+=''+( x )+'';t+='<\/div>';t+='<\/div>';t+='<div class=\"footer\">';t+=''+(spike.core.Message.get('ac_version'))+'';t+='<\/div>';t+='<\/div>'; return t;};