import re

def clean(file_pathname, out_pathname):
    content = [line.strip() for line in open(file_pathname)]
    cleaned = []
    for line in content:
        if len(line.strip()) is not 0:
            cleaned.append(re.match(r"^\d+\.\t(.*?)( \(\d+ words\)|)$", line).group(1))
    with open(out_pathname, 'a') as file:
        file.write('\n'.join(cleaned))

if __name__ == '__main__':
    clean('../resource/documents.txt', '../resource/docs.txt')
