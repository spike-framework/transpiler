package com.spike.transpiler.model;

import com.spike.templates.TemplateCompiler;
import com.spike.transpiler.dependencies.DependencyConstructor;
import com.spike.transpiler.serialization.Serializer;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public void compile() throws Exception {

        StringBuilder compiledBuilder = new StringBuilder();
        for (SpikePackage spikePackage : this.packages) {
            spikePackage.compile();
            compiledBuilder.append(spikePackage.compiled);
        }

        this.compiled = compiledBuilder.toString();
        this.collectDependencies();
        this.compileConstructorUsages();
        this.compileSuperUsages();
        this.collectTotalNamespaces();
        this.compileGlobalVariables();
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
                    this.compiled = this.compiled.replace(matchedConstructor, "new " + baseConstructor + "([" + arguments+"]");
                }
            }


        }

    }

    private void compileSuperUsages() {

        Pattern p = Pattern.compile("(this\\.super\\([^\\)]*)");
        Matcher m = p.matcher(this.compiled);
        while (m.find()) {

            String matchedConstructor = m.group();

            String baseConstructor = matchedConstructor.substring(matchedConstructor.indexOf("new") + 3, matchedConstructor.indexOf("(")).trim();
            String arguments = matchedConstructor.substring(matchedConstructor.indexOf("(") + 1, matchedConstructor.length()).trim();
            String argumentsCleaned = this.getConstructorUsageArguments(arguments);

            int argumentsCount = argumentsCleaned.length() == 0 ? 0 : argumentsCleaned.split(",").length;

            this.compiled = this.compiled.replace(matchedConstructor,"this.super.constructor_" + argumentsCount + ".apply(this,[" + arguments+"]");

        }

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

    private void compileGlobalVariables() throws Exception {

        System.out.println("enters");
        System.out.println(this.compiled.contains("@if"));

        List<String> conditionsToReplace = new ArrayList<>();

        Pattern p = Pattern.compile("(?s)(?<=@if).*?(?=@endif)");
        Matcher m = p.matcher(this.compiled);
        while (m.find()) {

            String matchedCondition = m.group();

            System.out.println("matchedCondition : "+matchedCondition);

            String[] split = matchedCondition.split("\n");
            String condition = split[0];

            System.out.println("condition : "+condition);

            conditionsToReplace.add(condition);
            condition = condition.replaceAll("ENV", "'"+TemplateCompiler.ENV+"'").replaceAll("PROJECT", "'"+TemplateCompiler.PROJECT+"'");

            ScriptEngineManager factory = new ScriptEngineManager();
            ScriptEngine engine = factory.getEngineByName("JavaScript");
            try {
                engine.eval("var result = false; if("+condition+"){ result = true;} ");
                Boolean result = (Boolean) engine.get("result");

                System.out.println("condition : "+condition);
                System.out.println("result : "+result);

                if(!result){
                    this.compiled = this.compiled.replace(matchedCondition, "");
                }

            } catch (ScriptException e) {
                e.printStackTrace();
                throw new Exception("Spike Transpiler: Cannot eval condition "+condition);
            }


        }

        this.compiled = this.compiled.replaceAll("@if","").replaceAll("@endif","");

        for(String condition : conditionsToReplace){
            this.compiled = this.compiled.replace(condition, "");
        }


    }


}
