$targetDir = "c:\Users\qingguang\dev\QingNote\QingNote\app\src\main\java\dev"
$oldPkg = "com.ldlywt.note"
$newPkg = "com.qingguang.qingnote"

$files = Get-ChildItem -Recurse -Filter "*.kt" -Path $targetDir
$count = 0

foreach ($f in $files) {
    $bytes = [System.IO.File]::ReadAllBytes($f.FullName)
    $content = [System.Text.Encoding]::UTF8.GetString($bytes)
    if ($content.Contains($oldPkg)) {
        $newContent = $content.Replace($oldPkg, $newPkg)
        $newBytes = [System.Text.Encoding]::UTF8.GetBytes($newContent)
        [System.IO.File]::WriteAllBytes($f.FullName, $newBytes)
        $count++
    }
}

Write-Host "Replaced package name in $count files under dev/"
