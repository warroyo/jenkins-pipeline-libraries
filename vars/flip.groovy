def call(def cdParams) {

    env. ROUTE = "${cdParams.domain}"

        echo 'Flipping Traffic....'
        def flip = libraryResource "com/warroyo/pipeline/scripts/flip.sh"
        writeFile file: "flip.sh", text: flip
        sh "chmod +x flip.sh"
        sh script: './flip.sh', label: 'flipping traffic'
}