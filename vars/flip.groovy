def call(def cdParams) {

    env. ROUTE = "${cdParams.domain}"

    node{
        echo 'Flipping Traffic....'
        def flip = libraryResource "com/warroyo/pipeline/scripts/flip.sh"
        writeFile file: "flip.sh", text: flip
        sh script: './flip.sh', label: 'flipping traffic'
    }
}