import { readFileSync } from 'node:fs';

// Repomix 1.18.1 retains this Javadoc when both compression and comment removal are enabled.
const source = readFileSync(process.argv[2], 'utf8').replaceAll('\r\n', '\n');
const declaration = '\tpublic String getLicenseAgreedForVersion() {';
const documentedDeclaration = [
  '\t/**',
  '\t * @return Returns the licenseAgreedForVersion.',
  '\t */',
  declaration,
].join('\n');

process.stdout.write(source.replace(documentedDeclaration, declaration));
