def call(def cdParams) {

    contents = readYaml (file: "${cdParams.envFile}") 
    echo contents.domain.toString()
    env.ROUTE = contents.domain.toString()

    echo 'Flipping Traffic....'
    def flip = libraryResource "com/warroyo/pipeline/scripts/flip.sh"
    writeFile file: "flip.sh", text: flip
    sh "chmod +x flip.sh"
    sh script: './flip.sh', label: 'flipping traffic'
}