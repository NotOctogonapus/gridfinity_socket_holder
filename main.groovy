File plateUnitFile = ScriptingEngine.fileFromGit("https://github.com/NotOctogonapus/gridfinity_socket_holder.git", "isolated box plate v1.stl");
CSG plateUnit  = Vitamins.get(plateUnitFile);
return plateUnit