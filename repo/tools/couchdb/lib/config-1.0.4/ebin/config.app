{application,config,
             [{description,"INI file configuration system for Apache CouchDB"},
              {vsn,"1.0.4"},
              {registered,[config,config_event]},
              {applications,[kernel,stdlib]},
              {mod,{config_app,[]}},
              {modules,[config,config_app,config_listener,config_listener_mon,
                        config_notifier,config_sup,config_util,
                        config_writer]}]}.
