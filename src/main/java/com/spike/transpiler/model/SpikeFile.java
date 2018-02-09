package com.spike.transpiler.model;

import com.spike.transpiler.dependencies.DependencyConstructor;
import com.spike.transpiler.serialization.Serializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpikeFile {

    public static int TOTAL_NAMESPACES = 0;

    public List<ExtendingModel> extendingMap = new ArrayList<>();
    public HashMap<String, List<String>> constructorsMap;
    public List<SpikePackage> packages = new ArrayList<>();

    public String body = null;
    public String compiled = null;

    public SpikeFile(String body) {
        this.body = body.trim();
        this.constructorsMap = Serializer.deserializeConstructors();
        this.createPackages();
    }

    public void compile() {

        StringBuilder compiledBuilder = new StringBuilder();
        for (SpikePackage spikePackage : this.packages) {
            spikePackage.compile();
            compiledBuilder.append(spikePackage.compiled);
        }

        this.compiled = compiledBuilder.toString();
        this.collectDependencies();
        this.compileConstructorUsages();
        this.compileConstructorsMap();
        this.collectTotalNamespaces();
    }

    private void createPackages() {

        String[] packagesDeclarations = this.body.split("package");

        for (String packageBody : packagesDeclarations) {

            packageBody = packageBody.trim();

            if (!packageBody.equals("package") && packageBody.length() > 0) {
                this.packages.add(new SpikePackage(this, "package " + packageBody));
            }

        }

    }

    private void collectTotalNamespaces() {
        this.compiled = "spike.core.Assembler.resetNamespaces(" + TOTAL_NAMESPACES + ", '"+this.packages.get(0).packageName+"');" + this.compiled;
    }

    private void compileConstructorUsages() {

        Pattern p = Pattern.compile("(new*\\s+[^\\)]*)");
        Matcher m = p.matcher(this.compiled);
        while (m.find()) {

            String matchedConstructor = m.group();
            String baseConstructor = matchedConstructor.substring(matchedConstructor.indexOf("new") + 3, matchedConstructor.indexOf("(")).trim();
            String arguments = matchedConstructor.substring(matchedConstructor.indexOf("(") + 1, matchedConstructor.length()).trim();
            String argumentsCleaned = this.getConstructorUsageArguments(arguments);

            int argumentsCount = argumentsCleaned.length() == 0 ? 0 : argumentsCleaned.split(",").length;

            if (this.constructorsMap.get(baseConstructor) != null) {

                String foundConstructor = this.constructorsMap.get(baseConstructor).get(argumentsCount);

                if (foundConstructor == null) {
                    try {
                        throw new Exception("No matching constructor found : " + matchedConstructor);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    this.compiled = this.compiled.replace(matchedConstructor, "new " + foundConstructor + "(" + arguments);
                }
            }


        }

    }

    private String getConstructorArgumentsCount(String constructorFullName){

        String[] split = constructorFullName.split("_");

        if(split.length > 1){
            return split[1];
        }

        return "0";

    }

    private void compileConstructorsMap(){

        StringBuilder constructorsMapBuilder = new StringBuilder();

        constructorsMapBuilder.append("spike.core.Assembler.setConstructorsMap({");
        for (Map.Entry<String, List<String>> baseClass : this.constructorsMap.entrySet()) {

            constructorsMapBuilder
                    .append("'")
                    .append(baseClass.getKey())
                    .append("':{");

            for(String constructorFullName : baseClass.getValue()){

                constructorsMapBuilder
                        .append("'")
                        .append(this.getConstructorArgumentsCount(constructorFullName))
                        .append("':'")
                        .append(constructorFullName)
                        .append("',");

            }

            constructorsMapBuilder.append("},");


        }

        constructorsMapBuilder.append("});");

        this.compiled = this.compiled + constructorsMapBuilder.toString();

    }

    private String getConstructorUsageArguments(String arguments){

        arguments = this.removeFromParenthesis(arguments, "{}");
        arguments = this.removeFromParenthesis(arguments, "[]");

        return arguments;
    }


    private String removeFromParenthesis(String str, String parenthesis){
        if(parenthesis.contains("[]")){
            return str.replaceAll("\\s*\\[[^\\]]*\\]\\s*", " ");
        }else if(parenthesis.contains("{}")){
            return str.replaceAll("\\s*\\{[^\\}]*\\}\\s*", " ");
        }else{
            return str.replaceAll("\\s*\\([^\\)]*\\)\\s*", " ");
        }
    }

    private void collectDependencies() {

        HashMap<String, List<String>> extendsClassesMap = new HashMap<>();

        for(ExtendingModel extendingModel : this.extendingMap){

            if(extendsClassesMap.get(extendingModel.extendsTo) == null){
                extendsClassesMap.put(extendingModel.extendsTo, new ArrayList<String>());
            }

            extendsClassesMap.get(extendingModel.extendsTo).add(extendingModel.extendsFrom);

        }

        DependencyConstructor dependencyConstructor = new DependencyConstructor();
        dependencyConstructor.generateDependencies(this.extendingMap, extendsClassesMap, this);
        this.compiled = this.compiled + dependencyConstructor.compiled;

    }

    private void minifyPackageNames() {


        //zrobić minifikacje nazw paczek, czyli przeleciec po paczkach i np wszystkie spike.core zamienic na 's.c' etc
        //zeby zmniejszyc rozmiar plikow


    }
}
